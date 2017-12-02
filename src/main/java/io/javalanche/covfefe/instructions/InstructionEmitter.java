package io.javalanche.covfefe.instructions;

import static org.bytedeco.javacpp.LLVM.LLVMBasicBlockAsValue;

import io.javalanche.covfefe.CompileContext;
import io.javalanche.covfefe.CovfefeMain;
import io.javalanche.covfefe.RegisterReferences;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bytedeco.javacpp.LLVM;
import org.bytedeco.javacpp.LLVM.LLVMBasicBlockRef;
import org.bytedeco.javacpp.LLVM.LLVMValueRef;

@FunctionalInterface
public interface InstructionEmitter {

  Map<String, InstructionEmitter> emitterMap = Map.ofEntries(
      Map.entry("nop", InstructionNop::nop),
      Map.entry("br", InstructionBranch::branch),
      Map.entry("cbr", InstructionBranch::condition),
      Map.entry("add", InstructionArithmetic::add),
      Map.entry("sub", InstructionArithmetic::sub),
      Map.entry("div", InstructionArithmetic::div),
      Map.entry("mul", InstructionArithmetic::mul),
      Map.entry("and", InstructionComparison::and),
      Map.entry("or", InstructionComparison::or),
      Map.entry("cmp_EQ", InstructionComparison::cmp_eq),
      Map.entry("cmp_NE", InstructionComparison::cmp_ne),
      Map.entry("cmp_LE", InstructionComparison::cmp_le),
      Map.entry("cmp_GE", InstructionComparison::cmp_ge),
      Map.entry("cmp_LT", InstructionComparison::cmp_lt),
      Map.entry("cmp_GT", InstructionComparison::cmp_gt)
  );

  void sink(CompileContext ctx, String instruction);

  static LLVMValueRef[] parse(CompileContext ctx, String pattern, String instruction) {
    List<Integer> valTypes = new ArrayList<>();
    StringBuilder regex = new StringBuilder();

    int pIdx = 0;
    while (pIdx < pattern.length()) {
      if ('%' == pattern.charAt(pIdx)) {
        pIdx++;
        if ('%' == pattern.charAt(pIdx)) {
          regex.append('%');
        }
        else if ('r' == pattern.charAt(pIdx)) {
          regex.append("(r\\d+)");
          valTypes.add(0);
        }
        else if ('c' == pattern.charAt(pIdx)) {
          regex.append("(\\d+)");
          valTypes.add(1);
        }
        else if ('l' == pattern.charAt(pIdx)) {
          regex.append("(\\w+)");
          valTypes.add(2);
        }
        else {
          System.err.println("Unknown pattern type %" + pattern.charAt(pIdx));
          System.err.println("(This is a compiler programmer error)");
          System.exit(1);
        }
      }
      else {
        regex.append(pattern.charAt(pIdx));
      }
      pIdx++;
    }

    Matcher match = Pattern.compile(regex.toString())
        .matcher(String.join(" ", instruction));
    if (!match.matches()) {
      System.err.printf("Instruction \"%s\" did not match pattern \"%s\"", instruction, regex);
      System.exit(1);
    }

    LLVMValueRef[] values = new LLVMValueRef[valTypes.size()];

    for (int valIdx = 0; valIdx < values.length; valIdx++) {
      if (valTypes.get(valIdx) == 0) {
        values[valIdx] = RegisterReferences.getRef(ctx, match.group(valIdx + 1));
      }
      else if (valTypes.get(valIdx) == 1) {
        values[valIdx] = LLVM.LLVMConstInt(
            LLVM.LLVMInt32Type(), Integer.parseInt(match.group(valIdx + 1)), 0);
      }
      else if (valTypes.get(valIdx) == 2) {
        values[valIdx] = LLVMBasicBlockAsValue(CovfefeMain.blocks.get(match.group(valIdx + 1)));
      }
    }

    return values;
  }
}
