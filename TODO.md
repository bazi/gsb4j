
# TODO

- make main API interface return list of threats
- adopt java 11:
  - http client
- sometimes spotbugs report is not included when "mvn site" is run


# Release steps

- update README (update version here first before releasing!!!)
- deploy to staging using profile "release"
  `mvn -P release release:clean release:prepare`
  `mvn -P release release:perform`
- analyze, close, and release to central
- upload bundles to Github releases page

mvn sonar:sonar -P sonar -Dsonar.login=dfb66c636d1d7ae153c59f8d6d21cfedb6853e1e
