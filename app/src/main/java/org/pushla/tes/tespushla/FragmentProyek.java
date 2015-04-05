package org.pushla.tes.tespushla;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import org.pushla.util.ReportSender;

import java.util.zip.Inflater;

/**
 * Created by Anjar_Ibnu on 31/03/2015.
 */
public class FragmentProyek extends Fragment {

    public RecyclerView rv;
    public ProyekAdapter pa;
    LayoutInflater inflater;
    ProgressBar pb;
    View rootView;
    FragmentManager fragmentManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        fragmentManager = getFragmentManager();
        if(SplashScreen.listProyek != null && !SplashScreen.listProyek.isEmpty())
        {
            final View rootView = inflater.inflate(R.layout.list_proyek, container, false);
            rv = (RecyclerView) rootView.findViewById(R.id.proyek_list);
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(container.getContext());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);

            pa = new ProyekAdapter(SplashScreen.listProyek);
            rv.setAdapter(pa);
            this.rootView = rootView;
            return rootView;
        }
        else
        {
            final View rootView = inflater.inflate(R.layout.no_internet, container, false);
            this.rootView = rootView;
            Button tryAgain = (Button) rootView.findViewById(R.id.try_again);
            pb = (ProgressBar) rootView.findViewById(R.id.progress_bar_no_internet);
            pb.setVisibility(View.INVISIBLE);
            tryAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pb.setVisibility(View.VISIBLE);
                    refreshList();
                }
            });
            return rootView;
        }
    }

    private void refreshList()
    {
        new ReportSender.GetProyek(rootView.getContext(), this).execute();
    }

    public void reload()
    {
        pb.setVisibility(View.INVISIBLE);
        fragmentManager.beginTransaction().replace(R.id.frame_container, new FragmentProyek()).commit();
    }
}
