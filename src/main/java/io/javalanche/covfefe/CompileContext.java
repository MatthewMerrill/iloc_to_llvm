package io.javalanche.covfefe;

import org.bytedeco.javacpp.LLVM.LLVMBuilderRef;
import org.bytedeco.javacpp.LLVM.LLVMModuleRef;

public class CompileContext {

  public final LLVMModuleRef moduleRef;
  public final LLVMBuilderRef builderRef;

  public CompileContext(LLVMModuleRef moduleRef, LLVMBuilderRef builderRef) {
    this.moduleRef = moduleRef;
    this.builderRef = builderRef;
  }

}
