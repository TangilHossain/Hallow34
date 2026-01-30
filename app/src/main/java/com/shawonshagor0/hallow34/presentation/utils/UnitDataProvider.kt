package com.shawonshagor0.hallow34.presentation.utils

import android.content.Context
import com.shawonshagor0.hallow34.R

object UnitDataProvider {

    fun getMainUnits(context: Context): List<String> {
        return context.resources.getStringArray(R.array.main_units).toList()
    }

    fun getSubUnits(context: Context, mainUnit: String): List<String> {
        val resId = getSubUnitResourceId(context, mainUnit)
        return if (resId != 0) {
            context.resources.getStringArray(resId).toList()
        } else {
            emptyList()
        }
    }

    private fun getSubUnitResourceId(context: Context, mainUnit: String): Int {
        // Map main units to their sub-unit array resource names
        val resourceName = when (mainUnit) {
            "পুলিশ হেডকোয়ার্টার্স" -> null // No sub-units
            "এসবি ঢাকা" -> "sub_units_এসবি_ঢাকা"
            "সিআইডি" -> "sub_units_সিআইডি"
            "পিবিআই" -> "sub_units_পিবিআই"
            "পুলিশ হাসপাতাল, রাজারবাগ" -> null // No sub-units
            "পুলিশ টেলিকম" -> "sub_units_পুলিশ_টেলিকম"
            "মেট্রোপলিটন পুলিশ" -> "sub_units_মেট্রোপলিটন_পুলিশ"
            "ঢাকা রেঞ্জ" -> "sub_units_ঢাকা_রেঞ্জ"
            "চট্টগ্রাম রেঞ্জ" -> "sub_units_চট্টগ্রাম_রেঞ্জ"
            "খুলনা রেঞ্জ" -> "sub_units_খুলনা_রেঞ্জ"
            "রাজশাহী রেঞ্জ" -> "sub_units_রাজশাহী_রেঞ্জ"
            "বরিশাল রেঞ্জ" -> "sub_units_বরিশাল_রেঞ্জ"
            "সিলেট রেঞ্জ" -> "sub_units_সিলেট_রেঞ্জ"
            "রংপুর রেঞ্জ" -> "sub_units_রংপুর_রেঞ্জ"
            "ময়মনসিংহ রেঞ্জ" -> "sub_units_ময়মনসিংহ_রেঞ্জ"
            "এপিবিএন" -> "sub_units_এপিবিএন"
            "RAB" -> "sub_units_RAB"
            "হাইওয়ে পুলিশ" -> "sub_units_হাইওয়ে_পুলিশ"
            "ট্যুরিস্ট পুলিশ" -> "sub_units_ট্যুরিস্ট_পুলিশ"
            "নৌ পুলিশ" -> null // No sub-units defined
            "এন্টি টেররিজম ইউনিট (ATU)" -> null // No sub-units defined
            "রেলওয়ে পুলিশ" -> "sub_units_রেলওয়ে_পুলিশ"
            "এমআরটি" -> null // No sub-units defined
            "ইন্ডাস্ট্রিয়াল পুলিশ" -> "sub_units_ইন্ডাস্ট্রিয়াল_পুলিশ"
            "ট্রেনিং সেন্টার" -> "sub_units_ট্রেনিং_সেন্টার"
            else -> null
        }

        return if (resourceName != null) {
            context.resources.getIdentifier(resourceName, "array", context.packageName)
        } else {
            0
        }
    }

    // Get sub-units for metropolitan police sub-units (like DMP, CMP, etc.)
    fun getMetropolitanSubUnits(context: Context, metropolitanUnit: String): List<String> {
        val resourceName = when (metropolitanUnit) {
            "ডিএমপি" -> "sub_units_ডিএমপি"
            "সিএমপি" -> "sub_units_সিএমপি"
            "কেএমপি" -> "sub_units_কেএমপি"
            "আরএমপি" -> "sub_units_আরএমপি"
            "এসএমপি" -> "sub_units_এসএমপি"
            "রংপুর মেট্রোপলিটন" -> "sub_units_রংপুর_মেট্রোপলিটন"
            "বিএমপি" -> "sub_units_বিএমপি"
            "জিএমপি" -> "sub_units_জিএমপি"
            else -> null
        }

        return if (resourceName != null) {
            val resId = context.resources.getIdentifier(resourceName, "array", context.packageName)
            if (resId != 0) context.resources.getStringArray(resId).toList() else emptyList()
        } else {
            emptyList()
        }
    }

    // Get sub-units for range districts
    fun getRangeDistrictSubUnits(context: Context, district: String): List<String> {
        // Replace spaces with underscores for resource name matching
        val resourceName = "sub_units_${district.replace(" ", "_")}"
        val resId = context.resources.getIdentifier(resourceName, "array", context.packageName)
        return if (resId != 0) {
            context.resources.getStringArray(resId).toList()
        } else {
            emptyList()
        }
    }
}
