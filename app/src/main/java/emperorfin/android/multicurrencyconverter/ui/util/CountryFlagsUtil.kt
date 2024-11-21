package emperorfin.android.multicurrencyconverter.ui.util

import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import emperorfin.android.multicurrencyconverter.ui.model.currencyrate.Country
import java.io.IOException
import java.util.Base64

object CountryFlagsUtil {

    private const val CHAR_SET_UTF_8: String = "UTF-8"
    private const val DELIMITER_COMMA: String = ","

    private fun loadCountriesFromAsset(assets: AssetManager): List<Country>? {

        val listOfCountries: List<Country>?

        try {
            val stream = assets.open("countries.json")
            val size = stream.available()
            val buffer = ByteArray(size)

            stream.read(buffer)
            stream.close()

            val stringJson = String(buffer, charset(CHAR_SET_UTF_8))
            val gson = Gson()
            val customListType = object : TypeToken<List<Country>>(){}.type

            listOfCountries = gson.fromJson(stringJson, customListType)

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return listOfCountries
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFlagImageBitMap(base64String: String): ImageBitmap {
        var encodedString = base64String

        if(encodedString.contains(DELIMITER_COMMA)) {
            encodedString = encodedString.split(DELIMITER_COMMA)[1]
        }

        val decodedByteArray = Base64.getDecoder()
            .decode(encodedString.toByteArray(charset(CHAR_SET_UTF_8)))
        val bitMap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)

        return bitMap.asImageBitmap()
    }

    fun loadMapOfCurrencySymbolToFlag(assets: AssetManager): MutableMap<String, String> {
        val mapOfCurrencySymbolToFlag = mutableMapOf<String, String>()
        val countries: List<Country>? = loadCountriesFromAsset(assets)

        countries?.forEach {
            mapOfCurrencySymbolToFlag[it.currency.code] = it.flag
        }

        return mapOfCurrencySymbolToFlag
    }

}