package de.codingforcelm.idmp.fragment;

import androidx.fragment.app.Fragment;

public class NameAwareFragment extends Fragment {

    private String fragmentname = this.getClass().getSimpleName();

    protected void setFragmentname(String name) {
        this.fragmentname = name;
    }

    public String getFragmentname() {
        return fragmentname;
    }

}
