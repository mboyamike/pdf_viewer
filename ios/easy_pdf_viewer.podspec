#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint easy_pdf_viewer.podspec` to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'easy_pdf_viewer'
  s.version          = '0.0.1'
  s.summary          = 'Flutter plugin for viewing PDFs by rendering pages to images.'
  s.description      = <<-DESC
Renders PDF pages to images for display on Android and iOS. Supports loading from file, URL, and assets.
                       DESC
  s.homepage         = 'https://github.com/kaichii/pdf_viewer'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Kaichi' => 'contact@kaichi.dev' }
  s.source           = { :path => '.' }
  s.source_files     = 'Classes/**/*.{h,m}'
  s.dependency 'Flutter'
  s.platform = :ios, '13.0'

  # Flutter.framework does not contain a i386 slice.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'i386' }

  # If your plugin requires a privacy manifest, for example if it uses any
  # required reason APIs, update the PrivacyInfo.xcprivacy file to describe your
  # plugin's privacy impact, and then uncomment this line. For more information,
  # see https://developer.apple.com/documentation/bundleresources/privacy_manifest_files
  # s.resource_bundles = {'easy_pdf_viewer_privacy' => ['Resources/PrivacyInfo.xcprivacy']}
end
