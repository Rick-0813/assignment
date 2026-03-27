public abstract class LibraryItem{

    private String itemId;
    private String title ;
    private String isbn ;
    private boolean isAvailable ;
    
    //Constructor
    public LibraryItem(String itemId ,  String title , String isbn){
        this.itemId = itemId;
        this.title  =title ;
        this.isbn = isbn ;
        this.isAvailable = true ;
    }


    //getter
    public String getItemId() {
        return itemId;
    }
    public String getTitle() {
        return title ;
    }
    public String getIsbn() {
        return isbn ;
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