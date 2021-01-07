package de.codingforcelm.idmp.fragment;

import androidx.fragment.app.Fragment;

public class NameAwareFragment extends Fragment {

    private String fragmentname = this.getClass().getSimpleName();

    public String getFragmentname() {
        return fragmentname;
    }

    protected void setFragmentname(String name) {
        this.fragmentname = name;
    }

}
