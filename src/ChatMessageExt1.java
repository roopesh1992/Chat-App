import java.io.*;

public class ChatMessageExt1 extends ChatMessage implements Serializable{
        public String flag;
        public int lastX,lastY,x,y,id;
        public String color;
    
        public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public void setId(int id){
          this.id = id;
        }
        
        public int getId(){
          return(id);
        }

        public void setFlag(String flag){
           this.flag = flag;
        }
  
        public String getFlag(){
          return flag;
        }
        public void setlastX(int lastX){
           this.lastX = lastX;
        }
  
        public int getlastX(){
          return lastX;
        }
        public void setlastY(int lastY){
           this.lastY = lastY;
        }
  
        public int getlastY(){
          return lastY;
        }
        public void setx(int x){
           this.x = x;
        }
  
        public int getx(){
          return x;
        }
        public void sety(int y){
           this.y = y;
        }
  
        public int gety(){
          return y;
        }
        
}