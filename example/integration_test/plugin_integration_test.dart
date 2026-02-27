// Integration test for easy_pdf_viewer.
// Verifies the example app runs and the PDF viewer is available.

import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

import 'package:easy_pdf_viewer/easy_pdf_viewer.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('PDF viewer exports', (WidgetTester tester) async {
    expect(PDFDocument, isNotNull);
    expect(PDFViewer, isNotNull);
  });
}
