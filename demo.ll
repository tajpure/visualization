; ModuleID = './src/test/resources/scheme/print.scm'

@.str = global [13 x i8] c"hello world!\00"

define double @main() {
entry:
  %call = call double @printf(i8* getelementptr inbounds ([13 x i8]* @.str, i32 0, i32 0))
  ret double %call
}

declare double @printf(i8*)