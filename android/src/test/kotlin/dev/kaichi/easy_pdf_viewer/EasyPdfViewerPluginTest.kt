package dev.kaichi.easy_pdf_viewer

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.junit.Test

/**
 * Unit tests for EasyPdfViewerPlugin.
 * Run from plugin android dir with: ./gradlew test
 */
internal class EasyPdfViewerPluginTest {

    @Test
    fun pluginInstantiates() {
        val plugin = EasyPdfViewerPlugin()
        val call = MethodCall("unknownMethod", null)
        val mockResult = object : MethodChannel.Result {
            override fun success(result: Any?) {}
            override fun error(errorCode: String, message: String?, details: Any?) {}
            override fun notImplemented() {}
        }
        plugin.onMethodCall(call, mockResult)
    }
}
