package com.example.encryption.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import com.example.android_advanced_kotlin.activity.model.Card
import com.example.android_advanced_kotlin.activity.model.User
import com.example.android_mvc.network.RetrofitHttp
import com.example.encryption.R
import com.pdp.encryption.utils.Asymmetric
import com.pdp.encryption.utils.Symmetric
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class MainActivity : AppCompatActivity() {
//    val privateServerKey = "MIIEoAIBADANBgkqhkiG9w0BAQEFAASCBIowggSGAgEAAoH8Nx/GJcFfILU7+Z8+zZ0YwIwTcVGjlopixDJkKdyYdTWyV+TPClNU00LPmOnOnv850YwRFHLd+LYL7bejdEKcOEgDlMhjEYfaUaPU6lK6lgwv7wg7GDPsW2IqSdCVoyBHBfBYmk1U5UU5kHrRi5hKX9hpZ73RQvGIvxsdEDCeF6cLPy7mDjN1VIaeJmrv9lbBM+8PplkarRgZv38FW4lzTkUFEBk7Go6YEZZ+v6peWAgXAErCaora6bTK3juZvqM8aErj2yjWG5pMj0itMsDbHpGqmKYoAuO+8hWFOnscLAtc1HQWpD5h1r5ZvCDwcyu4cqzgdNLaMotESeZVAgMBAAECgfwODTlzcmGPcuOA9EkMHNmsa4ihQFBVFZ2rqbTCvwrWBgQaR6dHWIqs98D3l9Il0aWpduz7q3RN7AoKZN9zyW8NIb5X9OTcGSdb3ElwGfqaOS0W36BAwSuk99cTzen3FUlFiEjtvHzBnO3ZmJVF4mJDIGVuHoLzb6KOJJk6AUpu53U9Nsvg2kkGCrDrKrUkkTx19KU0YoADloyoyP9xO/zPBJRlN7cJGt4Ajjg+lDQyO6QuFqwR3yWPMgRd1mezdMS3WAWdPKvGuD2ZeCS89YUlTgrtLdtXhlizId+TGEWZoHxOqRTHzg/O1t0lwAbmwGDwBR23PL3fuNpxAIECfnhUKf+kvJ57DjupzA1cbs6T0qhJ0ZTZeIaDDYloJ6EeLCPDKKYbH19E3wmsJj5gtSUprCrP2JoQZt7fUUFwBR6q/H32m4QVzDKfuWF5cuM9BAbdFW/PUJkRU6m3J6ZMhZBGsgkfzG+iB85wE5EwOocX7oPw5D2pp3CruQCWEQJ+dUbdIZDTiU8B3OOzouKNDscdYzRJurV5qe3z6volUU5oQAgDzKVRccQFSopAxYCpYDrYKIWy6oKdQhkfBStckonyMO4ehHWOMdvUleSWBa5JHrScGQsuAKTXsQWJqn7vlIi4qPuK81F8QIiwUmV/uaRrgD+m0113VvaG/3gFAn5PsQ4PrX7uRvlqMk5eGvWYAvblVd5kApN8Ipd4hW6Zmm4JUVs+h4ADjI1azpSVg171OeA4imcdwfcfbC9Yc2Qwp3WJxyXAGN+gN4CDOfgI34QnN4zW/CqY9Yy/PfoYfV2H7ApFWLYAyQL6ieerpJFen07sO0IuRIoMaO6hoMECfhuWJ74ViyzpYM7M4RyXngaz373ONumsdon6Zz4GyXBuuScWu44P9TCFo5j9HG9Y7H6uWNpvWL7BKqy5rApTQNtXh5jq6lLONDyWMVNxCbkcXibS5UUe1BtiqzoAy3lHkqOl5YoaYX0K1ed4P8GiDoFko8TvNBbUDZZvNyIw4QJ+YOoqxQPsluOZNxdY4GmZKEQCAwec+dcESq3G1CYb5P+ebmg5zjau+GtLHrlkVL8xQuV2NNkUGS6cfwOkOUcu3p6I1Qdyq4P80tfe1HNARwyPhQ4I846Z8hprbxX2ku8sZRWUGghSaA0UxQn9QOToCyST0BJdCihsmiHZT1kO"
//    val publicMobileKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7SrR7pvsp1rMDVn4R9XjZp4q6IVSit9XAKvAMgTIml7vAp2H81RA9V4jDyrFlASrvxq0uGi5W/bZj+AdYGu1ceTDeLK8OvH90GyKXaPiHjuPAeRCGyTBkn+7nogVxqpmrGVGUJfcslr4JawU/9Vou7LjdraNStCnldRJzKvcY+wIDAQAB"

    companion object {
        val TRANSFORMATION = "AES/GCM/NoPadding"
        val ANDROID_KEY_STORE = "AndroidKeyStore"
        val SAMPLE_ALIAS = "MYALIAS"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder("MyKeyAlias",
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()

        val pair = encryptData("Test this encryption")

        val decryptedData = decryptData(pair.first, pair.second)

        val encrypted = pair.second.toString(Charsets.UTF_8)
        Log.d("@@Encrypted data:", encrypted)
        Log.d("@@Decrypted data:", decryptedData)
        Log.d("@@KeySet",getKey().toString())
        initViews()
    }

    fun encryptData(data: String): Pair<ByteArray, ByteArray> {
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")

        var temp = data
        while (temp.toByteArray().size % 16 != 0)
            temp += "\u0020"

        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val ivBytes = cipher.iv
        val encryptedBytes = cipher.doFinal(temp.toByteArray(Charsets.UTF_8))

        return Pair(ivBytes, encryptedBytes)
    }

    fun decryptData(ivBytes: ByteArray, data: ByteArray): String{
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        val spec = IvParameterSpec(ivBytes)

        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        return cipher.doFinal(data).toString(Charsets.UTF_8).trim()
    }

    fun getKey(): SecretKey {
        val keystore = KeyStore.getInstance("AndroidKeyStore")
        keystore.load(null)

        val secretKeyEntry = keystore.getEntry("MyKeyAlias", null) as KeyStore.SecretKeyEntry
        return secretKeyEntry.secretKey
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        val b_save_user = findViewById<Button>(R.id.b_save_user)
        val b_load_user = findViewById<Button>(R.id.b_load_user)
        val b_save_card = findViewById<Button>(R.id.b_save_card)
        val b_load_card = findViewById<Button>(R.id.b_load_card)
        b_save_user.setOnClickListener {
            val user = User("Firdavs", "+998909998899")
            user.fullName = Symmetric.encrypt(user.fullName)!!
            user.phoneNumber = Symmetric.encrypt(user.phoneNumber)!!
            Log.d("TAG", user.toString())
            apiSaveUser(user)
//            testSymmetric()
        }
        b_load_user.setOnClickListener {
            apiLoadUser()
        }
        b_save_card.setOnClickListener {
            val card = Card("Mukaddam Khamraeva","8678761526789012")
            card.cardNumber = Asymmetric.encryptMessage(card.cardNumber, getKey().toString())
            Log.d("TAG",card.toString())
            apiSaveCard(card)
            testAsymmetric()
        }
        b_load_card.setOnClickListener {
            apiLoadCard()
        }
    }

    private fun apiSaveUser(user: User) {
        RetrofitHttp.userService.createUser(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                Log.d("TAG", response.toString())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("TAG", t.toString())
            }
        })
    }

    private fun apiLoadUser() {
        RetrofitHttp.userService.getUserById(1).enqueue(object : Callback<User> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()
                Log.d("TAG", Symmetric.decrypt(user!!.fullName).toString())
                Log.d("TAG", Symmetric.decrypt(user.phoneNumber).toString())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("TAG", t.toString())
            }
        })
    }

    private fun apiSaveCard(card: Card) {
        RetrofitHttp.cardService.createCard(card).enqueue(object : Callback<Card> {
            override fun onResponse(call: Call<Card>, response: Response<Card>) {
                Log.d("TAG", response.toString())
            }

            override fun onFailure(call: Call<Card>, t: Throwable) {
                Log.d("TAG", t.toString())
            }
        })
    }

    private fun apiLoadCard() {
        RetrofitHttp.cardService.getCardById(1).enqueue(object : Callback<Card> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<Card>, response: Response<Card>) {
                val card = response.body()
                val cardNumber = Asymmetric.decryptMessage(card!!.cardNumber, getKey().toString())
                Log.d("TAG", cardNumber)
            }

            override fun onFailure(call: Call<Card>, t: Throwable) {
                Log.d("TAG", t.toString())
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testSymmetric() {
        // secret text
        val originalString = "\nI got my driver's license last week\n" +
                "just like we always talked about\n" +
                "cause you were so excited for me \n" +
                "to finally drive up to your house\n" +
                "but today i drove through the suburbs\n" +
                "cry'n 'cause you weren't around\n" +
                "\n" +
                "And you probably with that blonde girl\n" +
                "who always made me doubt\n" +
                "she is so much older than \n" +
                "she is everything I'm insecure about\n" +
                "yeah today drove through the suburbs\n" +
                "cause how could i ever love someone else?\n" +
                "\n" +
                "And i know we weren't perfect but i've never felt this way for no one\n" +
                "And i just can't imagine how you could be so okay, now that i'm gone\n" +
                "Guess you didn't mean what you wrote in that song about me\n" +
                "cause you said forever now I drive alone past your street\n" +
                "\n" +
                "all my friends are tired \n" +
                "of hearing how i miss you, but\n" +
                "i kinda feel sorry for them \n" +
                "cause they'll never know you the way that I do \n" +
                "yet today i drove through the suburb\n" +
                "and pictured i was driving home to you\n" +
                "\n" +
                "And i know we weren't perfect but i've never felt this way for no one\n" +
                "And i just can't imagine how you could be so okay, now that i'm gone\n" +
                "Guess you didn't mean what you wrote in that song about me\n" +
                "cause you said forever now I drive alone past your street\n" +
                "\n" +
                "red lights, stop signs, i still see your face in the white cars, front yards\n"+
                " can't drive past the places we used to go to \n" +
                "cause i still fucking love you babe\n" +
                "ooh, ooh ...\n"+
                "sidewalks we crossed, \n" +
                "i still hear your voice in the traffics, we are laughing\n" +
                "over all the noise\n" +
                "God i'm so blue, know we are through\n" +
                "But i still fucking love you babe\n" +
                "ooh, ooh...\n" +
                "\n" +
                "I know we weren't perfect but i never felt this way for no one\n" +
                "and i just can't imagine how you could be so okay, now that i'm gone\n" +
                "guess you didn't mean what you wrote in that song about me\n" +
                "Cause you said forever now I drive alone past your street\n" +
                "yeah you said forever now i drive alone past your street\n"
        // Encryption
        val encryptedString = Symmetric.encrypt(originalString)
        // Decryption
        val decryptedString = Symmetric.decrypt(encryptedString)
        // Printing originalString,encryptedString,decryptedString
        Log.d("@@@", "Original String:$originalString")
        Log.d("@@@", "Encrypted value:$encryptedString")
        Log.d("@@@", "Decrypted value:$decryptedString")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun testAsymmetric() {
        val secretText = "red lights, stop signs, i still see your face in the white cars, front yards"
        val keyPairGenerator = Asymmetric()
        // Generate private and public key
        val privateKey: String = Base64.getEncoder().encodeToString(keyPairGenerator.privateKey.encoded)
        val publicKey: String = Base64.getEncoder().encodeToString(keyPairGenerator.publicKey.encoded)
        Log.d("@@@", "Private Key: $privateKey")
        Log.d("@@@", "Public Key: $publicKey")
        // Encrypt secret text using public key
        val encryptedValue = Asymmetric.encryptMessage(secretText, publicKey)
        Log.d("@@@", "Encrypted Value: $encryptedValue")
        // Decrypt
        val decryptedText = Asymmetric.decryptMessage(encryptedValue, privateKey)
        Log.d("@@@", "Decrypted output \n: $decryptedText")
    }


}