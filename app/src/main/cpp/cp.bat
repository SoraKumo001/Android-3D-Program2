rem @echo off
xcopy /D /Y  include\aflMath.*      ..\..\..\include\
xcopy /D /Y  include\afl3DBase.h    ..\..\..\include\ 
xcopy /D /Y  LibSrc\afl3DBase.cpp  ..\..\..\LibSrc\ 
xcopy /D /Y  include\aflOpenGL*.h   ..\..\..\include\ 
xcopy /D /Y  LibSrc\aflOpenGL*.cpp ..\..\..\LibSrc\ 
xcopy /D /Y  include\aflStd.h       ..\..\..\include\ 
xcopy /D /Y  LibSrc\aflStd.cpp     ..\..\..\LibSrc\ 
xcopy /D /Y  LibSrc\afl3DObject.cpp     ..\..\..\LibSrc\ 
xcopy /D /Y  Main.h        ..\..\..\OpenGL\GLTest\src\
xcopy /D /Y  Main.cpp      ..\..\..\OpenGL\GLTest\src\
xcopy /D /Y  Unit.h        ..\..\..\OpenGL\GLTest\src\
xcopy /D /Y  Unit.cpp      ..\..\..\OpenGL\GLTest\src\
xcopy /D /Y  Effect.h        ..\..\..\OpenGL\GLTest\include\
xcopy /D /Y  Effect.cpp      ..\..\..\OpenGL\GLTest\src\
xcopy /D /Y  Camera.h        ..\..\..\OpenGL\GLTest\include\
xcopy /D /Y  Camera.cpp      ..\..\..\OpenGL\GLTest\src\
