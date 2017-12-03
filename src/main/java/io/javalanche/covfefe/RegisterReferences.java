package io.javalanche.covfefe;

import static org.bytedeco.javacpp.LLVM.*;

import java.util.*;
import org.bytedeco.javacpp.LLVM;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;

public class RegisterReferences {

  private static final Map<String, LLVMValueRef> registerRefs = new HashMap<>();

  public static LLVMValueRef getRef(CompileContext ctx, String refName) {
    if (!registerRefs.containsKey(refName)) {
      registerRefs
          .put(refName, LLVMAddGlobal(ctx.moduleRef, LLVMInt32Type(), refName));
      LLVMSetInitializer(registerRefs.get(refName), LLVMConstInt(LLVMInt32Type(), 0, 0));
    }
    return registerRefs.get(refName);
  }

  public static List<String> getUsedRegisters() {
    List<String> used = new ArrayList<>(registerRefs.keySet());
    used.sort(Comparator.comparing(s -> Integer.parseInt(s.substring(1))));
    return used;
  }
}
