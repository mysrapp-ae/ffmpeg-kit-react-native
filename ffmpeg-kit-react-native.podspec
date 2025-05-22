require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ffmpeg-kit-react-native"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platform          = :ios
  s.requires_arc      = true
  s.static_framework  = true

  s.source       = { :git => "https://github.com/mysrapp-ae/ffmpeg-kit-react-native.git", :tag => "react.native.v#{s.version}-mysrapp" }

  s.default_subspec   = 'https'

  s.dependency "React-Core"

  s.subspec 'https' do |ss|
    ss.source_files      = '**/FFmpegKitReactNativeModule.{m,h}'
    ss.dependency 'mysrapp-ffmpeg-kit-ios-https', "6.0.3"
    ss.ios.deployment_target = '12.1'
  end
end
