package com.anythink.custom.adapter;

import android.util.Log;

import com.rixengine.api.AlxAdConstant;
import com.rixengine.api.AlxBannerView;
import com.rixengine.api.AlxInterstitialAD;
import com.rixengine.api.AlxRewardVideoAD;
import com.rixengine.api.AlxSplashAd;
import com.rixengine.api.nativead.AlxNativeAd;
import com.secmtp.sdk.core.api.ATAdConst;
import com.secmtp.sdk.core.api.ATBiddingNotice;
import com.secmtp.sdk.core.api.ATInitMediation;

import java.util.HashMap;
import java.util.Map;

public class AlxBiddingNotice implements ATBiddingNotice {

    private static final String TAG = "AlxBiddingNotice";

    private Object adObject;


    protected AlxBiddingNotice(Object adObject) {
        this.adObject = adObject;
    }


    @Override
    public void notifyBidWin(double costPrice, double secondPrice, Map<String, Object> extra) {
        Log.i(TAG, "notifyBidWin: winPrice=" + costPrice + ";secondPrice=" + secondPrice);
        Map<String, Object> map = new HashMap<>();
        map.put(AlxAdConstant.KEY.BIDDING_SECOND_PRICE, secondPrice);

        try {
            if (adObject instanceof AlxRewardVideoAD) {
                AlxRewardVideoAD ad = (AlxRewardVideoAD) adObject;
                ad.reportBiddingWin(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxInterstitialAD) {
                AlxInterstitialAD ad = (AlxInterstitialAD) adObject;
                ad.reportBiddingWin(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxBannerView) {
                AlxBannerView ad = (AlxBannerView) adObject;
                ad.reportBiddingWin(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxSplashAd) {
                AlxSplashAd ad = (AlxSplashAd) adObject;
                ad.reportBiddingWin(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxNativeAd) {
                AlxNativeAd ad = (AlxNativeAd) adObject;
                ad.reportBiddingWin(map);
                return;
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void notifyBidLoss(String lossCode, double winPrice, Map<String, Object> extra) {
        //判断竞胜方渠道
        int adnId = ATInitMediation.getIntFromMap(extra, ATBiddingNotice.ADN_ID, AlxAdConstant.BIDDING_ADN_ID.OTHER);
        switch (adnId) {
            case ATAdConst.BIDDING_ADN_ID.LOSE_TO_NORMAL_IN_SAME_ADN:
                adnId = AlxAdConstant.BIDDING_ADN_ID.SAME_TO_NORMAL;
                break;
            case ATAdConst.BIDDING_ADN_ID.LOSE_TO_HB_IN_SAME_ADN:
                adnId = AlxAdConstant.BIDDING_ADN_ID.SAME_TO_BIDDING;
                break;
            case ATAdConst.BIDDING_ADN_ID.LOSE_TO_OWN_ADN:
                adnId = AlxAdConstant.BIDDING_ADN_ID.OWN;
                break;
            case ATAdConst.BIDDING_ADN_ID.LOSE_TO_OTHER_ADN:
                adnId = AlxAdConstant.BIDDING_ADN_ID.OTHER;
                break;
        }
        Log.i(TAG, "notifyBidLoss: lossCode=" + lossCode + ";winPrice=" + winPrice + ";adnId=" + adnId);


        Map<String, Object> map = new HashMap<>();
        map.put(AlxAdConstant.KEY.BIDDING_WIN_PRICE, winPrice);
        map.put(AlxAdConstant.KEY.BIDDING_LOSS_REASON, lossCode);
        map.put(AlxAdConstant.KEY.BIDDING_ADN_ID, adnId);

        try {
            if (adObject instanceof AlxRewardVideoAD) {
                AlxRewardVideoAD ad = (AlxRewardVideoAD) adObject;
                ad.reportBiddingLoss(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxInterstitialAD) {
                AlxInterstitialAD ad = (AlxInterstitialAD) adObject;
                ad.reportBiddingLoss(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxBannerView) {
                AlxBannerView ad = (AlxBannerView) adObject;
                ad.reportBiddingLoss(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxSplashAd) {
                AlxSplashAd ad = (AlxSplashAd) adObject;
                ad.reportBiddingLoss(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxNativeAd) {
                AlxNativeAd ad = (AlxNativeAd) adObject;
                ad.reportBiddingLoss(map);
                return;
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public void notifyBidDisplay(boolean isWin, double displayPrice) {
        Log.i(TAG, "notifyBidDisplay: isWin=" + isWin + ";displayPrice=" + displayPrice);
        Map<String, Object> map = new HashMap<>();
        map.put(AlxAdConstant.KEY.BIDDING_WIN_PRICE, displayPrice);

        try {
            if (adObject instanceof AlxRewardVideoAD) {
                AlxRewardVideoAD ad = (AlxRewardVideoAD) adObject;
                ad.reportBiddingImpression(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxInterstitialAD) {
                AlxInterstitialAD ad = (AlxInterstitialAD) adObject;
                ad.reportBiddingImpression(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxBannerView) {
                AlxBannerView ad = (AlxBannerView) adObject;
                ad.reportBiddingImpression(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxSplashAd) {
                AlxSplashAd ad = (AlxSplashAd) adObject;
                ad.reportBiddingImpression(map);
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            if (adObject instanceof AlxNativeAd) {
                AlxNativeAd ad = (AlxNativeAd) adObject;
                ad.reportBiddingImpression(map);
                return;
            }
        } catch (Throwable ignored) {
        }
    }

    @Override
    public ATAdConst.CURRENCY getNoticePriceCurrency() {
        return ATAdConst.CURRENCY.USD;
    }
}
