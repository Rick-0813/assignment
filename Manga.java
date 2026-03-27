public class Manga extends LibraryItem {

    private String illustrator ;
    private int volumeNumber ;
    private int stockQuantity ;

    //Constructor
    public Manga (String itemId , String title , String isbn , String illustrator , int volumeNumber , int stockQuantity) {
        super(itemId , title , isbn);
        this.illustrator = illustrator ;
        this.volumeNumber = volumeNumber ;
        this.stockQuantity   = stockQuantity;

        if (this.stockQuantity > 0) {
            setAvailable(true);
        }
        else{
            setAvailable(false);
        }
    }


    //Getter
    public String getIllustrator() {
        return illustrator;
    }
    public int getVolumeNumber() {
        return volumeNumber;
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
        System.out.println("  -----Manga Details-----");
        System.out.println("  Manga ID : " + getItemId());
        System.out.println("  Title : " + getTitle());
        System.out.println("  Isbn : " + getIsbn());
        System.out.println("  Illustrator : " + illustrator);
        System.out.println("  Volume  : Vol. " + volumeNumber);
        System.out.println("  Stock Quantity : " + stockQuantity);
        if (isAvailable()){
            System.out.println("  Status : Available");
        }
        else {
            System.out.println("  Status : Out of Stock");
        }
        System.out.println("  --------------------------");
    }

    @Override
    public String toFileString() {
        return "Manga, " + getItemId()+ " ," + getTitle() + " ," + getIsbn() + " ," + illustrator + " ," + volumeNumber + " ," + stockQuantity ;
    }
}
