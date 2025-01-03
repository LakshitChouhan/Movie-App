package com.example.movieapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.movieapp.R
import com.example.movieapp.data.movieDetails.MovieDetailsActivity
import com.example.movieapp.database.Movies
import com.example.movieapp.util.Constants.BASE_IMAGE_URL

class MoviesAdapter(
    private val moviesList: MutableList<Movies>, private val context: Context
) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>(){

    private val density: Float = context.resources.displayMetrics.density
    private val moviesDataList: MutableList<Movies> = moviesList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val movieView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_movies_content,parent,false)
        return MovieViewHolder(movieView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        holder.apply {
            movieName.text = moviesDataList[position].original_title
            val releaseDate: List<String>  = moviesDataList[position].release_date.split('-').toList()
            movieYear.text = releaseDate[0]
            id.text = moviesDataList[position].id.toString()
            ratingBar.rating = moviesDataList[position].vote_average / 2
        }

        Glide.with(context)
            .load(BASE_IMAGE_URL + moviesDataList[position].poster_path)
            .transform(RoundedCorners((20 * density).toInt()))
            .into(holder.poster)
    }

    override fun getItemCount(): Int {
        return moviesDataList.size
    }

    fun addMovies(newMovies: List<Movies>) {
        val startPos = moviesDataList.size
        moviesDataList.addAll(newMovies)
        notifyItemRangeInserted(startPos, newMovies.size)
    }

    inner class MovieViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val poster : ImageView = itemView.findViewById(R.id.poster)
        val movieName : TextView = itemView.findViewById(R.id.movieName)
        val movieYear : TextView = itemView.findViewById(R.id.movieYear)
        val id : TextView = itemView.findViewById(R.id.movieId)
        val ratingBar : RatingBar = itemView.findViewById(R.id.rating_bar)

        init {
            itemView.setOnClickListener{
                val movieDetailsIntent = Intent(it.context, MovieDetailsActivity:: class.java)
                movieDetailsIntent.putExtra("id", itemView.findViewById<TextView>(R.id.movieId).text)
                startActivity(it.context,movieDetailsIntent,null)
            }
        }

    }
}