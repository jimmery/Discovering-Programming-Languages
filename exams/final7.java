/* This will probably only be a comment file. 

a. Which of these are advantages of throwing an exception over simply 
   returning a distiguished error value (null)? 

   (i.) Exceptions can cause program execution to be terminated. 
   ii. Exceptions clearly distinguish erroneous from normal behaviour. 
   iii. Exceptions implicitly propagate up the dynamic call chain to handler. 
   iv. Exceptions can contain additional imformation about error. 

b. Consider a successfully typechecked Java method void m() throws Exn1, Exn2
   Which are true? 

   (iii.) While this seems true, it's not guaranteed. Not sure why you would
          call a throw if it doesn't throw. :P
   iv. If Exn3 is thrown during the execution of m, it will definitely be 
       caught before the method ends. 

c. Same as b. Which are true? 

   iii. Java requires every caller of m to either catch exceptions of type 
        Exn1, or to include Exn1 or a supertype of Exn1 in its throws clause. 

*/
