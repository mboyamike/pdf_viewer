package dev.kaichi.easy_pdf_viewer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.os.Process
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File
import java.io.FileOutputStream

/** EasyPdfViewerPlugin */
class EasyPdfViewerPlugin : FlutterPlugin, MethodCallHandler {

    private var channel: MethodChannel? = null
    private var binding: FlutterPlugin.FlutterPluginBinding? = null
    private var backgroundHandler: Handler? = null
    private val pluginLocker = Any()
    private val filePrefix = "FlutterEasyPdfViewerPlugin"

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "easy_pdf_viewer_plugin")
        channel!!.setMethodCallHandler(this)
        binding = flutterPluginBinding
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        synchronized(pluginLocker) {
            if (backgroundHandler == null) {
                val handlerThread = HandlerThread(
                    "flutterEasyPdfViewer",
                    Process.THREAD_PRIORITY_BACKGROUND
                )
                handlerThread.start()
                backgroundHandler = Handler(handlerThread.looper)
            }
        }

        val mainHandler = Handler(Looper.getMainLooper())

        backgroundHandler!!.post {
            when (call.method) {
                "getNumberOfPages" -> {
                    val filePath = call.argument<String>("filePath")
                    val clearCacheDir = call.argument<Boolean>("clearCacheDir") ?: false
                    val numResult = getNumberOfPages(filePath, clearCacheDir)
                    mainHandler.post { result.success(numResult) }
                }
                "getPage" -> {
                    val filePath = call.argument<String>("filePath")
                    val pageNumber = call.argument<Int>("pageNumber") ?: 1
                    val pageRes = getPage(filePath, pageNumber)
                    mainHandler.post { result.success(pageRes) }
                }
                "clearCacheDir" -> {
                    clearCacheDir()
                    mainHandler.post { result.success(null) }
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun clearCacheDir() {
        try {
            val directory = binding?.applicationContext?.cacheDir ?: return
            val files = directory.listFiles { _, name ->
                name.lowercase().startsWith(filePrefix.lowercase())
            } ?: return
            for (file in files) {
                file.delete()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getNumberOfPages(filePath: String?, clearCacheDir: Boolean): String? {
        if (filePath == null) return null
        val pdf = File(filePath)
        return try {
            if (clearCacheDir) {
                clearCacheDir()
            }
            ParcelFileDescriptor.open(pdf, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    String.format("%d", renderer.pageCount)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    private fun getFileNameFromPath(name: String): String {
        val lastSlash = name.lastIndexOf('/')
        val withoutPath = name.substring(lastSlash + 1)
        val lastDot = withoutPath.lastIndexOf('.')
        val withoutExt = if (lastDot > 0) withoutPath.substring(0, lastDot) else withoutPath
        return "$filePrefix-$withoutExt"
    }

    @SuppressLint("DefaultLocale")
    private fun createTempPreview(bmp: Bitmap, name: String, page: Int): String? {
        val fileNameOnly = getFileNameFromPath(name)
        return try {
            val fileName = String.format("%s-%d.png", fileNameOnly, page)
            val file = File.createTempFile(fileName, null, binding?.applicationContext?.cacheDir)
            FileOutputStream(file).use { out ->
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getPage(filePath: String?, pageNumber: Int): String? {
        if (filePath == null) return null
        val pdf = File(filePath)
        return try {
            ParcelFileDescriptor.open(pdf, ParcelFileDescriptor.MODE_READ_ONLY).use { pfd ->
                PdfRenderer(pfd).use { renderer ->
                    val pageCount = renderer.pageCount
                    if (pageCount == 0) return@use null
                    val pageIndex = (pageNumber - 1).coerceIn(0, pageCount - 1)
                    val page = renderer.openPage(pageIndex)
                    try {
                        val width = page.width.toDouble()
                        val height = page.height.toDouble()
                        val docRatio = width / height
                        val targetWidth = 2048.0
                        val targetHeight = (targetWidth / docRatio).toInt()
                        val bitmap = Bitmap.createBitmap(
                            targetWidth.toInt(),
                            targetHeight,
                            Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        canvas.drawColor(Color.WHITE)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        createTempPreview(bitmap, filePath, pageIndex + 1)
                    } finally {
                        page.close()
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
        channel = null
        this.binding = null
    }
}
