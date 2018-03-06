@echo off
echo %cd%
set JAVA_8=d:\DevTools\jdk1.8.0_51\bin
echo %JAVA_8%/java -d64 -Xmx256m -Xms256m -Dtestfx.robot=glass -Dglass.platform=Monocle -Dmonocle.platform=Headless -Dprism.order=sw -Dheadless.geometry=1280x1024-32 -jar %cd%/tools/Sentinel/sentinel-1.0.jar -config %cd%/tools/Sentinel/default_config.cfg -timeout 300
rem %JAVA_8%/java -d64 -Xmx256m -Xms256m -Dtestfx.robot=glass -Dglass.platform=Monocle -Dmonocle.platform=Headless -Dprism.order=sw -Dheadless.geometry=1280x1024-32 -jar %cd%/tools/Sentinel/sentinel-1.0.jar -config %cd%/tools/Sentinel/default_config.cfg -timeout 300
%JAVA_8%/java -d64 -Xmx256m -Xms256m -jar %cd%/tools/Sentinel/sentinel-1.0.jar -config %cd%/tools/Sentinel/default_config.cfg -timeout 300