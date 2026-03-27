public class Novel extends LibraryItem {

    private String author ;
    private String genre ;
    private int stockQuantity ;

    //Constructor
    public Novel (String itemId , String title , String isbn , String author , String genre , int stockQuantity) {
        super(itemId , title , isbn);
        this.author = author ;
        this.genre = genre ;
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
    public String getGenre() {
        return genre ;
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
        System.out.println("  -----Novel Details-----");
        System.out.println("  ID : " + getItemId());
        System.out.println("  Title : " + getTitle());
        System.out.println("  ISBN : " + getIsbn());
        System.out.println("  Author : " +author);
        System.out.println("  Genre : " + genre);
        System.out.println("  Stock Quantity : " + stockQuantity);
        if (isAvailable()){
            System.out.println("  Status : Available");
        }
        else {
            System.out.println("  Status : Out of Stock");
        }
        System.out.println("  ---------------------");
    }

    @Override
    public String toFileString() {
        return "Novel, " + getItemId() + " ," + getTitle() + " ," + getIsbn() + " ," + author + " ," + genre  + " ," + stockQuantity ;
    }
}
