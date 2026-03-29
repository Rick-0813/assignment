public class SelfHelp extends LibraryItem {

    private String author ;
    private String topic ;
    private int stockQuantity ;

    public SelfHelp (String itemId , String title , String isbn , String author , String topic , int stockQuantity) {
        super(itemId , title ,isbn);
        this.author = author ;
        this.topic = topic ;
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
    public String getTopic() {
        return topic ;
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
        System.out.println("  -----SelfHelp Details-----");
        System.out.println("  StoryBook ID : " + getItemId());
        System.out.println("  Title : " + getTitle());
        System.out.println("  ISBN : " + getIsbn());
        System.out.println("  Author : " + author);
        System.out.println("  Topic : " + topic);
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
        return "SelfHelp," + getItemId() + "," + getTitle() + "," + getIsbn() + "," + author + "," + topic + "," + stockQuantity;
    }
}
