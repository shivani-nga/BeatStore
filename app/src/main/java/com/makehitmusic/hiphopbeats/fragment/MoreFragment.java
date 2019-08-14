package com.makehitmusic.hiphopbeats.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.makehitmusic.hiphopbeats.R;
import com.makehitmusic.hiphopbeats.view.LoginScreen;
import com.makehitmusic.hiphopbeats.view.SplashScreen;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MoreFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MoreFragment extends Fragment {

    /** Tag for log messages */
    private static final String LOG_TAG = MoreFragment.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MoreFragment newInstance() {
        MoreFragment fragment = new MoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static class LedgerPreferenceFragment extends PreferenceFragment {

        String urlLicensing = "https://makehitmusic.net/apps/mogul/faqs/";
        String urlApps = "https://itunes.apple.com/us/app/make-hit-music-hip-hop-studio/id1438351352?ls=1&mt=8";
        String urlSell = "https://pages.convertkit.com/ea2996f0bb/ca7fa96488";
        String urlIdea = "https://my.appideatoreality.com";
        String urlReview = "https://play.google.com/store/apps/details?id=com.makehitmusic.hiphopbeats";
        String urlTerms = "https://makehitmusic.net/apps/mogul/terms-of-use/";
        String urlPolicy = "https://makehitmusic.net/apps/mogul/privacy-policy/";

        private GoogleSignInClient mGoogleSignInClient;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.server_client_id))
                    .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                    .requestEmail()
                    .build();

            // Build GoogleAPIClient with the Google Sign-In API and the above options.
            mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

            // Logout Preference Click Listener
            Preference logoutPreference = findPreference(getString(R.string.key_logout));
            logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {

                    SharedPreferences sharedPref = getActivity().getSharedPreferences(
                            getString(R.string.preference_login), Context.MODE_PRIVATE);
                    int loginTypeInt = sharedPref.getInt("LoginType", 0);
                    int userCode = sharedPref.getInt("UserCode", 0);
                    int userId = sharedPref.getInt("UserId", 0);
//                    if (loginTypeInt == 1 && userCode != 0 && userId != 0) {
//                        // Check for existing Google Sign In account, if the user is already signed in
//                        // the GoogleSignInAccount will be non-null.
//                        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
//                        if (account != null) {
//                            revokeAccess();
//                        } else {
//                            revokeAccess();
//                        }
//                    } else if (loginTypeInt == 2 && userCode != 0 && userId != 0) {
//                        revokeAccess();
//                    } else {
//                        revokeAccess();
//                    }
                    revokeAccess();

                    return true;
                }
            });

            // Restoring Preference Click Listener
            Preference restorePreference = findPreference(getString(R.string.key_purchases));
            restorePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity(), "Purchases have been restored", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            // Licensing Preference Click Listener
            Preference licensingPreference = findPreference(getString(R.string.key_license));
            licensingPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent licensingIntent = new Intent(Intent.ACTION_VIEW);
                    licensingIntent.setData(Uri.parse(urlLicensing));
                    startActivity(licensingIntent);
                    return true;
                }
            });

            // MHM App Store Click Listener
            Preference appsPreference = findPreference(getString(R.string.key_apps));
            appsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent appsIntent = new Intent(Intent.ACTION_VIEW);
                    appsIntent.setData(Uri.parse(urlApps));
                    startActivity(appsIntent);
                    return true;
                }
            });

            // Selling Click Listener
            Preference sellPreference = findPreference(getString(R.string.key_sell));
            sellPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent sellIntent = new Intent(Intent.ACTION_VIEW);
                    sellIntent.setData(Uri.parse(urlSell));
                    startActivity(sellIntent);
                    return true;
                }
            });

            // Idea Click Listener
            Preference ideaPreference = findPreference(getString(R.string.key_idea));
            ideaPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent ideaIntent = new Intent(Intent.ACTION_VIEW);
                    ideaIntent.setData(Uri.parse(urlIdea));
                    startActivity(ideaIntent);
                    return true;
                }
            });

            // Review Preference Click Listener
            Preference reviewPreference = findPreference(getString(R.string.key_review));
            reviewPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent reviewIntent = new Intent(Intent.ACTION_VIEW);
                    reviewIntent.setData(Uri.parse(urlReview));
                    startActivity(reviewIntent);
                    return true;
                }
            });

            // Contact Support Click Listener
            Preference supportPreference = findPreference(getString(R.string.key_support));
            supportPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent supportIntent = new Intent(Intent.ACTION_SEND);
                    supportIntent.setType("message/rfc822");
                    supportIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"support@makehitmusic.com"});
                    supportIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Support regarding BeatStore App");
                    startActivity(Intent.createChooser(supportIntent, "Send Feedback"));
                    return true;
                }
            });

            // Terms Click Listener
            Preference termsPreference = findPreference(getString(R.string.key_terms));
            termsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent termsIntent = new Intent(Intent.ACTION_VIEW);
                    termsIntent.setData(Uri.parse(urlTerms));
                    startActivity(termsIntent);
                    return true;
                }
            });

            // Policy Click Listener
            Preference policyPreference = findPreference(getString(R.string.key_policy));
            policyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Intent policyIntent = new Intent(Intent.ACTION_VIEW);
                    policyIntent.setData(Uri.parse(urlPolicy));
                    startActivity(policyIntent);
                    return true;
                }
            });

        }

        public void revokeAccess() {
            mGoogleSignInClient.revokeAccess()
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            signOut();
                        }
                    });
        }

        public void signOut() {
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            resetEverything();
                        }
                    });
        }

        public void resetEverything() {
            SharedPreferences sharedPref = getActivity().getSharedPreferences(
                    getString(R.string.preference_login), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("LoginType", 0);
            editor.putInt("UserCode", 0);
            editor.putInt("UserId", 0);
            editor.putString("UserName", "null");
            editor.putString("UserEmail", "null");
            editor.apply();

            // Take the user to Login Screen
            Intent i = new Intent(getActivity(), LoginScreen.class);
            startActivity(i);
            Toast.makeText(getActivity(), "Signed Out: Successfully", Toast.LENGTH_SHORT).show();

            getActivity().finish();
        }

    }

}
