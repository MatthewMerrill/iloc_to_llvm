# iloc_to_llvm
iloc to llvm compiler

## What is this?
This was a project put together for Sacramento State's Local Hack Day 2017.
iloc is a fictional assembly language used as the target for programs written in Sacramento State's compilers course.
While it would work as an assembly langauge, there is no prior art for running Sac State's dialect of iloc... until today.

## How does it work?
The project parses the iloc instructions, builds an AST, and then traverses the AST to emit LLVM IR code.
The LLVM IR can be interpreted in-process or emitted to a file to be compiled and executed. 
