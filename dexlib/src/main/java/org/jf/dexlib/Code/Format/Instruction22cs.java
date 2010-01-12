/*
 * [The "BSD licence"]
 * Copyright (c) 2009 Ben Gruver
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jf.dexlib.Code.Format;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.TwoRegisterInstruction;
import org.jf.dexlib.Util.NumberUtils;
import org.jf.dexlib.Util.AnnotatedOutput;
import org.jf.dexlib.DexFile;

public class Instruction22cs extends Instruction implements TwoRegisterInstruction {
    public static final Instruction.InstructionFactory Factory = new Factory();
    private byte regA;
    private byte regB;
    private short fieldOffset;

    public Instruction22cs(Opcode opcode, byte regA, byte regB, int fieldOffset) {
        super(opcode);

        if (regA >= 1 << 4 ||
                regB >= 1 << 4) {
            throw new RuntimeException("The register number must be less than v16");
        }

        if (fieldOffset >= 1 << 16) {
            throw new RuntimeException("The field offset must be less than 65536");
        }

        this.regA = regA;
        this.regB = regB;
        this.fieldOffset = (short)fieldOffset;
    }

    private Instruction22cs(Opcode opcode, byte[] buffer, int bufferIndex) {
        super(opcode);

        this.regA = NumberUtils.decodeLowUnsignedNibble(buffer[bufferIndex + 1]);
        this.regB = NumberUtils.decodeHighUnsignedNibble(buffer[bufferIndex + 1]);
        this.fieldOffset = (short)NumberUtils.decodeUnsignedShort(buffer, bufferIndex + 2);
    }

    protected void writeInstruction(AnnotatedOutput out, int currentCodeAddress) {
        out.writeByte(opcode.value);
        out.writeByte((regB << 4) | regA);
        out.writeShort(fieldOffset);
    }

    public Format getFormat() {
        return Format.Format22cs;
    }

    public int getRegisterA() {
        return regA;
    }

    public int getRegisterB() {
        return regB;
    }

    public int getFieldOffset() {
        return fieldOffset & 0xFFFF;
    }

    private static class Factory implements Instruction.InstructionFactory {
        public Instruction makeInstruction(DexFile dexFile, Opcode opcode, byte[] buffer, int bufferIndex) {
            return new Instruction22cs(opcode, buffer, bufferIndex);
        }
    }
}
