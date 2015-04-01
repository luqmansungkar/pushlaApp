package org.pushla.tes.tespushla;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Anjar_Ibnu on 31/03/2015.
 */
public class FragmentProyek extends Fragment {

    public RecyclerView rv;
    public ProyekAdapter pa;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.list_proyek, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.proyek_list);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(container.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);

        pa = new ProyekAdapter(SplashScreen.listProyek);
        rv.setAdapter(pa);
        return rootView;
    }
}
