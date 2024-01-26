package com.jmeter.plugins;

import org.jacoco.agent.rt.internal_a70731f.asm.MethodVisitor;
import org.jacoco.agent.rt.internal_a70731f.core.instr.Instrumenter;
import org.jacoco.agent.rt.internal_a70731f.core.internal.instr.InstrSupport;
import org.jacoco.agent.rt.internal_a70731f.core.runtime.IExecutionDataAccessorGenerator;

import java.io.FileInputStream;
import java.io.IOException;

public class MainTest {

    private static final class AccessorGenerator
            implements IExecutionDataAccessorGenerator {

        long classId;

        @Override
        public int generateDataAccessor(final long classId,
                                        final String classname, final int probeCount,
                                        final MethodVisitor mv) {
            this.classId = classId;
            InstrSupport.push(mv, probeCount);
            mv.visitIntInsn(188, 4);
            return 1;
        }
    }

    public static void main(String[] args) throws IOException {
        AccessorGenerator accessorGenerator = new AccessorGenerator();
        Instrumenter instrumenter = new Instrumenter(accessorGenerator);
        FileInputStream is = new FileInputStream("C:\\Users\\xingyu0223\\Downloads\\DbQuotaServiceImpl.class");
        byte[] source = new byte[is.available()];
        is.read(source);
        instrumenter.instrument(source, "com/zto/zbase/manager/domain/database/service/impl/DbQuotaServiceImpl");
    }
}
