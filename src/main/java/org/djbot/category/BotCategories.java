package org.djbot.category;

import com.jagrosh.jdautilities.command.Command.Category;

public class BotCategories {
    public Category MusicCat() { return new Category("Music"); }
    public Category UserCat() { return new Category("User"); }
    public Category AdminCat() { return new Category("Admin"); }
    public Category OwnerCat() { return new Category("Owner"); }
}
