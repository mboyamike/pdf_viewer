import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:easy_pdf_viewer/easy_pdf_viewer.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const channel = MethodChannel('easy_pdf_viewer_plugin');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
      switch (methodCall.method) {
        case 'getNumberOfPages':
          return '5';
        case 'getPage':
          return '/cache/page-1.png';
        case 'clearCacheDir':
          return null;
        default:
          return null;
      }
    });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test('PDFViewer and PDFDocument are exported', () {
    expect(PDFViewer, isNotNull);
    expect(PDFDocument, isNotNull);
    expect(IndicatorPosition.values.length, 4);
  });
}
