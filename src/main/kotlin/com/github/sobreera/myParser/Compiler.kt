package com.github.sobreera.myParser

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.*

class Compiler : ClassLoader() {
    companion object {
        fun compile(program: Program) {
            val className = "MyLang"
            val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
            cw.visit(V1_5,
                ACC_PUBLIC + ACC_SUPER,
                className,
                null,
                "java/lang/Object",
                null)
            cw.visitSource("$className.java", null)

            cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null).apply {
                visitVarInsn(ALOAD, 0)
                visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
                visitInsn(RETURN)
                visitMaxs(-1, -1)
                visitEnd()
            }

            val mainFunc = program.program.filter { it is FunctionDeclaration && it.name == "main" }.firstOrNull() as FunctionDeclaration?
            if(mainFunc != null){
                cw.visitMethod(ACC_PUBLIC + ACC_STATIC, mainFunc.name, "([Ljava/lang/String;)V", null, null).apply {
                    visitCode()
                    visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;")
                    val putString = mainFunc.body.statements.filter { it is FunctionCall && it.functionDeclaration.name == "puts" }.firstOrNull() as FunctionCall?
                    if(putString != null && putString.parameters.first() is StringNode) {
                        visitLdcInsn((putString.parameters.first() as StringNode).value)
                        visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V")
                        visitInsn(RETURN)
                        visitMaxs(2,1)
                        visitEnd()
                    }
                }
            }
            cw.visitEnd()

            val code = cw.toByteArray()
            val loader = Compiler()
            val klass = loader.defineClass(className, code, 0, code.size)
//            klass.getMethods()[0].invoke(null, arrayOf(""))
            klass.getMethod("main", Array<String>::class.java).invoke(null, arrayOf(""))
//            val out = DataOutputStream(FileOutputStream("$className.class"))
//            out.write(cw.toByteArray())
//            out.flush()
//            out.close()
        }
    }
}