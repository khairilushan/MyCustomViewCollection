# MyCustomViewCollection

# CinemaSeatLayout
### TODO
- Add listener to expose selected seat to the user
- Handle fling gesture

### How To
Here is how you can use it on your Activity/ Fragment.

```
class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_seat)
        cinemaSeatLayout.setAdapter(CinemaSeatAdapter())
    }

    class CinemaSeatAdapter: CinemaSeatLayout.Adapter() {

        override fun numberOfRows() = 14

        override fun numberOfColumn(row: Int) = 17

        override fun cinemaGuideText(row: Int): String {
            // TODO Your logic to define row title for each row
            return ""
        }

        override fun componentFor(row: Int, column: Int): CinemaComponent? {
            // TODO Your logic to define the component. There're 3 type of supported component right now.
            // CinemaSeatLayout.Seat = For normal seat with 3 type of state (SEAT_STATE_AVAILABLE, SEAT_STATE_UNAVAILABLE, SEAT_STATE_SELECTED) 
            // CinemaSeatLayout.Text = For 1 full width line that contain text 
            // CinemaSeatLayout.Space = For put a gap/space between seat
            return null
        }

    }
}
```

