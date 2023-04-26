package jp.techacademy.hideaki.tanigawa.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import jp.techacademy.hideaki.tanigawa.apiapp.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nameTextView.text = intent.getStringExtra(KEY_NAME).toString()
        binding.webView.loadUrl(intent.getStringExtra(KEY_URL).toString())
        processStar()
    }

    companion object {
        private const val KEY_ID = "key_id"
        private const val KEY_NAME = "key_name"
        private const val KEY_IMAGEURL = "key_imageurl"
        private const val KEY_URL = "key_url"
        fun start(activity: Activity, id: String, name: String, imageUrl: String, url: String) {
            activity.startActivity(
                Intent(activity, WebViewActivity::class.java).putExtra(
                    KEY_ID,
                    id
                ).putExtra(
                    KEY_NAME,
                    name
                ).putExtra(
                    KEY_IMAGEURL,
                    imageUrl
                ).putExtra(
                    KEY_URL,
                    url
                )
            )
        }
    }

    /**
     * 星を表示しする処理
     */
    private fun processStar(){
        // 星の処理
        binding.favoriteImageView.apply {
            // お気に入り状態を取得
            val isFavorite = FavoriteShop.findBy(intent.getStringExtra(KEY_ID).toString()) != null

            // 白抜きの星を設定
            setImageResource(if (isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)

            val id = intent.getStringExtra(KEY_ID).toString()
            val name = intent.getStringExtra(KEY_NAME).toString()
            val imageUrl = intent.getStringExtra(KEY_IMAGEURL).toString()
            val url = intent.getStringExtra(KEY_URL).toString()

            // 星をタップした時の処理
            setOnClickListener {
                if (isFavorite) {
                    onDeleteFavorite(id)
                } else {
                    onAddFavorite(id,name,imageUrl,url)
                }
            }
        }
    }

    /**
     * お気に入りに追加する処理
     */
    private fun onAddFavorite(
        shopId: String,
        shopName: String,
        shopImageUrl: String,
        shopUrl: String
    ) {
        // お気に入り状態を取得
        val isFavorite = FavoriteShop.findBy(shopId) != null
        if(isFavorite){
            FavoriteShop.update(shopId, "1")
        }else{
            Log.d("TEST","もしかしてここ通った？")
            FavoriteShop.insert (FavoriteShop().apply {
                id = shopId
                name = shopName
                imageUrl = shopImageUrl
                url = shopUrl
                flag = "1"
            })
        }
        processStar()
    }

    /**
     * お気に入りから削除する処理
     */
    private fun onDeleteFavorite(id: String){
        showConfirmDeleteFavoriteDialog(id)
    }

    private fun showConfirmDeleteFavoriteDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                FavoriteShop.update(id, "0")
                processStar()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
            .show()
    }
}