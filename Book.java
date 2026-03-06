public class Book extends LibraryItem {

    private String author ;
    private String isbn ;
    private int stockQuantity ;

    public Book (String itemId , String title , String author , String isbn , int stockQuantity) {
        super(itemId , title);
        this.author = author ;
        this.isbn = isbn ;
        this.stockQuantity   = stockQuantity;

        if (this.stockQuantity > 0) {
            setAvailable(true);
        }
        else{
            setAvailable(false);
        }
    }


    //Getter
    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn ;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }


    //Setter
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        setAvailable(this.stockQuantity > 0 );
    }


    @Override
    public void displayItemDetails() {
        System.out.println("  -----Book Details-----");
        System.out.println("  Book ID : " + getItemId());
        System.out.println("  Title : " + getTitle());
        System.out.println("  Author : " + author);
        System.out.println("  ISBN : " + isbn);
        System.out.println("  Stock Quantity : " + stockQuantity);
        if (isAvailable()){
            System.out.println("  Status : Available");
        }
        else {
            System.out.println("  Status : Out of Stock");
        }
        System.out.println("  -----------------------");
    }

}
