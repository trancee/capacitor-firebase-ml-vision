
  Pod::Spec.new do |s|
    s.name = 'CapacitorFirebaseMlVision'
    s.version = '1.1.2'
    s.summary = 'Use machine learning in your apps to solve real-world problems.'
    s.license = 'MIT'
    s.homepage = 'https://github.com/trancee/capacitor-firebase-ml-vision.git'
    s.author = 'Philipp Grosswiler'
    s.source = { :git => 'https://github.com/trancee/capacitor-firebase-ml-vision.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end
