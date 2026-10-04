package net.tropicraft.core.client.encyclopedia;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SectionPage extends SimplePage {
    public SectionPage(String id) { super(id); }
    @Override
    public boolean isBookmark() { return true; }
    //@Override
    //public String getTitle() { return super.getTitle().replace("encyclopedia", "encyclopedia.section"); }
}

