public class StoryBook extends LibraryItem {

    private String author ;
    private String targetAge ;
    private int stockQuantity ;

    public StoryBook (String itemId , String title , String isbn , String author , String targetAge , int stockQuantity) {
        super(itemId , title ,isbn);
        this.author = author ;
        this.targetAge = targetAge ;
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

    public String getTargetAge() {
        return targetAge ;
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
        System.out.println("  -----StoryBook Details-----");
        System.out.println("  StoryBook ID : " + getItemId());
        System.out.println("  Title : " + getTitle());
        System.out.println("  ISBN : " + getIsbn());
        System.out.println("  Author : " + author);
        System.out.println("  Target Age : " + targetAge);
        System.out.println("  Stock Quantity : " + stockQuantity);
        if (isAvailable()){
            System.out.println("  Status : Available");
        }
        else {
            System.out.println("  Status : Out of Stock");
        }
        System.out.println("  -----------------------");
    }

    @Override
    public String toFileString() {
        return "StoryBook," + getItemId() + "," + getTitle() + "," + getIsbn() + "," + author + "," + targetAge + "," + stockQuantity;
    }

}
