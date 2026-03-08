public abstract class LibraryItem{

    private String itemId;
    private String title ;
    private boolean isAvailable ;
    
    //Constructor
    public LibraryItem(String itemId ,  String title){
        this.itemId = itemId;
        this.title  =title ;
        this.isAvailable = true ;
    }


    //getter
    public String getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title ;
    }

    public boolean isAvailable() {
        return isAvailable ;
    }


//setter
    public void setAvailable (boolean available){
        this.isAvailable = available ;
    }

    public abstract void  displayItemDetails() ;

    public abstract String toFileString();
}