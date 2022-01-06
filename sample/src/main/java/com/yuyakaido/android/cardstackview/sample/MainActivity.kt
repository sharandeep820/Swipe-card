package com.yuyakaido.android.cardstackview.sample

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.rbrooks.indefinitepagerindicator.IndefinitePagerIndicator
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), CardStackListener {

    private val drawerLayout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.card_stack_view) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(createSpots()) }
    val spots = ArrayList<Spot>()
    private lateinit var pagerIndicator: IndefinitePagerIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      //  setupNavigation()
        setupCardStackView()
        setupButton()

     //   pagerIndicator = findViewById(R.id.recyclerview_pager_indicator_horizontal)


    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
     //   Toast.makeText(this,"onCardDragging: d = ${direction.name}", Toast.LENGTH_SHORT).show()

    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
       // Toast.makeText(this,"onCardSwiped: p = ${manager.topPosition}", Toast.LENGTH_SHORT).show()
        if (manager.topPosition == adapter.itemCount) {
           //paginate()
            val cardView : CardView = findViewById(R.id.cardView)
            val button_container : RelativeLayout = findViewById(R.id.button_container)
            cardView.visibility = View.GONE
            button_container.visibility = View.GONE

        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
       // Toast.makeText(this,"onCardRewound: ${manager.topPosition}", Toast.LENGTH_SHORT).show()
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
        //Toast.makeText(this,"onCardCanceled: ${manager.topPosition}", Toast.LENGTH_SHORT).show()
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
        //Toast.makeText(this,"onCardAppeared: ($position) ${textView.text}", Toast.LENGTH_SHORT).show()

        val data1 : TextView = findViewById(R.id.data1)
        val address : TextView = findViewById(R.id.address)
        val matched : TextView = findViewById(R.id.matched)

        if(position<spots.size){
            data1.setText(spots[position].name)
            address.setText(spots[position].state + "," + spots[position].city)
            matched.setText(spots[position].courseTracks)
        }else{
            val cardView : CardView = findViewById(R.id.cardView)
            cardView.visibility = View.GONE
        }


        if(position<spots.size) {
            data1.setText(spots[position].name)
            address.setText(spots[position].state + "," + spots[position].city)
            matched.setText(spots[position].courseTracks)
        }

    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.item_name)
      /*  Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")

        val data1 : TextView = findViewById(R.id.data1)
        val address : TextView = findViewById(R.id.address)
        val matched : TextView = findViewById(R.id.matched)

        if(position<spots.size){
            data1.setText(spots[position].name)
            address.setText(spots[position].state + "," + spots[position].city)
            matched.setText(spots[position].courseTracks)
        }else{
            val cardView : CardView = findViewById(R.id.cardView)
            cardView.visibility = View.GONE
        }
*/
        //Toast.makeText(this,"onCardDisappeared: ($position) ${textView.text}", Toast.LENGTH_SHORT).show()
    }

   /* private fun setupNavigation() {
        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // DrawerLayout
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        actionBarDrawerToggle.syncState()
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        // NavigationView
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.reload -> reload()
                R.id.add_spot_to_first -> addFirst(1)
                R.id.add_spot_to_last -> addLast(1)
                R.id.remove_spot_from_first -> removeFirst(1)
                R.id.remove_spot_from_last -> removeLast(1)
                R.id.replace_first_spot -> replace()
                R.id.swap_first_for_last -> swap()
            }
            drawerLayout.closeDrawers()
            true
        }
    }*/

    private fun setupCardStackView() {
        initialize()
    }

    private fun setupButton() {
        val skip = findViewById<View>(R.id.skip_button)
        skip.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Left)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }

        val rewind = findViewById<View>(R.id.rewind_button)
        rewind.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                    .setDirection(Direction.Bottom)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(DecelerateInterpolator())
                    .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }

        val like = findViewById<View>(R.id.like_button)
        like.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                    .setDirection(Direction.Right)
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(AccelerateInterpolator())
                    .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()
        }
    }

    private fun initialize() {
        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(4)
        manager.setTranslationInterval(12.0f)
        manager.setScaleInterval(0.98f)
        manager.setSwipeThreshold(0.1f)
        manager.setMaxDegree(90.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    private fun paginate() {
        val old = adapter.getSpots()
        val new = old.plus(createSpots())
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun reload() {
        val old = adapter.getSpots()
        val new = createSpots()
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun addFirst(size: Int) {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                add(manager.topPosition, createSpot())
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun addLast(size: Int) {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            addAll(List(size) { createSpot() })
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeFirst(size: Int) {
        if (adapter.getSpots().isEmpty()) {
            return
        }

        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(manager.topPosition)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun removeLast(size: Int) {
        if (adapter.getSpots().isEmpty()) {
            return
        }

        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            for (i in 0 until size) {
                removeAt(this.size - 1)
            }
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun replace() {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            removeAt(manager.topPosition)
            add(manager.topPosition, createSpot())
        }
        adapter.setSpots(new)
        adapter.notifyItemChanged(manager.topPosition)
    }

    private fun swap() {
        val old = adapter.getSpots()
        val new = mutableListOf<Spot>().apply {
            addAll(old)
            val first = removeAt(manager.topPosition)
            val last = removeAt(this.size - 1)
            add(manager.topPosition, last)
            add(first)
        }
        val callback = SpotDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setSpots(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun createSpot(): Spot {
        return Spot(
                name = "Yasaka Shrine",
                state = "Chandigarh",
                city = "Kyoto",
                courseTracks="98% match",
                url = "https://source.unsplash.com/Xq1ntWruZQI/600x800",
                distance = "20km"
        )
    }

    private fun createSpots(): List<Spot> {
        spots.add(Spot(name = "Yasaka Shrine", state = "Chandigarh", city = "Kyoto", courseTracks="98% match", url = "https://source.unsplash.com/Xq1ntWruZQI/600x800", distance = "20km"))
        spots.add(Spot(name = "Fushimi Inari Shrine",state = "Chandigarh",courseTracks="89% match", city = "Kyoto", url = "https://source.unsplash.com/NYyCqdBOKwc/600x800", distance = "22km"))
        spots.add(Spot(name = "Bamboo Forest",state = "Chandigarh", city = "Kyoto",courseTracks="83% match", url = "https://source.unsplash.com/buF62ewDLcQ/600x800", distance = "25km"))
        spots.add(Spot(name = "Brooklyn Bridge",state = "Chandigarh", city = "New York",courseTracks="63% match", url = "https://source.unsplash.com/THozNzxEP3g/600x800", distance = "26km"))
        spots.add(Spot(name = "Empire State Building",state = "Chandigarh", city = "New York",courseTracks="57% match", url = "https://source.unsplash.com/USrZRcRS2Lw/600x800", distance = "29km"))
        spots.add(Spot(name = "The statue of Liberty",state = "Chandigarh", city = "New York",courseTracks="53% match", url = "https://source.unsplash.com/PeFk7fzxTdk/600x800", distance = "30km"))
        spots.add(Spot(name = "Louvre Museum",state = "Chandigarh",city = "Paris",courseTracks="46% match", url = "https://source.unsplash.com/LrMWHKqilUw/600x800", distance = "33km"))
        spots.add(Spot(name = "Eiffel Tower",state = "Chandigarh", city = "Paris",courseTracks="35% match", url = "https://source.unsplash.com/HN-5Z6AmxrM/600x800", distance = "36km"))
        spots.add(Spot(name = "Big Ben",state = "Chandigarh", city = "London",courseTracks="33% match", url = "https://source.unsplash.com/CdVAUADdqEc/600x800", distance = "38km"))
        spots.add(Spot(name = "Great Wall of China",state = "Chandigarh", city = "China",courseTracks="29% match", url = "https://source.unsplash.com/AWh9C-QjhE4/600x800", distance = "40km"))



        spots.add(Spot(name = "Yasaka Shrine", state = "Chandigarh", city = "Kyoto", courseTracks="98% match", url = "https://source.unsplash.com/Xq1ntWruZQI/600x800", distance = "20km"))
        spots.add(Spot(name = "Fushimi Inari Shrine",state = "Chandigarh",courseTracks="89% match", city = "Kyoto", url = "https://source.unsplash.com/NYyCqdBOKwc/600x800", distance = "22km"))
        spots.add(Spot(name = "Bamboo Forest",state = "Chandigarh", city = "Kyoto",courseTracks="83% match", url = "https://source.unsplash.com/buF62ewDLcQ/600x800", distance = "25km"))
        spots.add(Spot(name = "Brooklyn Bridge",state = "Chandigarh", city = "New York",courseTracks="63% match", url = "https://source.unsplash.com/THozNzxEP3g/600x800", distance = "26km"))
        spots.add(Spot(name = "Empire State Building",state = "Chandigarh", city = "New York",courseTracks="57% match", url = "https://source.unsplash.com/USrZRcRS2Lw/600x800", distance = "29km"))
        spots.add(Spot(name = "The statue of Liberty",state = "Chandigarh", city = "New York",courseTracks="53% match", url = "https://source.unsplash.com/PeFk7fzxTdk/600x800", distance = "30km"))
        spots.add(Spot(name = "Louvre Museum",state = "Chandigarh",city = "Paris",courseTracks="46% match", url = "https://source.unsplash.com/LrMWHKqilUw/600x800", distance = "33km"))
        spots.add(Spot(name = "Eiffel Tower",state = "Chandigarh", city = "Paris",courseTracks="35% match", url = "https://source.unsplash.com/HN-5Z6AmxrM/600x800", distance = "36km"))
        spots.add(Spot(name = "Big Ben",state = "Chandigarh", city = "London",courseTracks="33% match", url = "https://source.unsplash.com/CdVAUADdqEc/600x800", distance = "38km"))
        spots.add(Spot(name = "Great Wall of China",state = "Chandigarh", city = "China",courseTracks="29% match", url = "https://source.unsplash.com/AWh9C-QjhE4/600x800", distance = "40km"))

      /*  val data1 : TextView = findViewById(R.id.data1)
        val address : TextView = findViewById(R.id.address)
        val matched : TextView = findViewById(R.id.matched)
        data1.setText(spots[0].name)
        address.setText(spots[0].state+","+spots[0].city)
        matched.setText(spots[0].courseTracks)*/

        return spots
    }

}
