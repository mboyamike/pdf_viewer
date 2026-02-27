import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const channel = MethodChannel('easy_pdf_viewer_plugin');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
      if (methodCall.method == 'getNumberOfPages') return '3';
      return null;
    });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test('easy_pdf_viewer_plugin channel getNumberOfPages returns string', () async {
    final result = await channel.invokeMethod<String>(
      'getNumberOfPages',
      {'filePath': '/test.pdf', 'clearCacheDir': false},
    );
    expect(result, '3');
  });
}
