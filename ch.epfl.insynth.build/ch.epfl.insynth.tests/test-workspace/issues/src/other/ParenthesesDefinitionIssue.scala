package parenthesesdefinitionissue

class A {
  
  def mayHaveParen() = 5
  
  def noParen = 5
  
}

class ParenthesesIssue {
  
  def mayHaveParenThis(): Float = 6.0f
  
  def noParenThis: Float = 6.0f

  def main = {
    // local functions
    def mayHaveParenFunLocal(): Double = 6d
      
    def noParenLocal: Double = 6d
    
		val a = new A
		
		val i: Int =  /*!*/
		  ; // using this to separate stamements (InSynth will not infer the type properly otherwise)
		  
		val c: Double =  /*!*/
		  ;
		  
		val f: Float =  /*!*/
		  ;
  }
    
}
