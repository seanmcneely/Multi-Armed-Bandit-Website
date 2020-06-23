# Multi-Armed-Bandit-Website

Optimizing my personal website with multi-armed-bandit algorithms, an alternative to A/B testing! 

This framework is designed to accomodate testing any number of website versions against each other to determine which solicits the most user interaction or other reward. 

## Usage:
### Naming Conventions
To do site testing, we will have to create several versions of each webpage. Every html page filename should end with that site's version number. Site version 1's homepage should be index1.html. Version 2's homepage should be index2.html... etc.

### Actions and Reward: The Rewards Class
To use this MAB framework, you first must decide what actions on your webpage you want users to take. For example, you may want users to read your mission statement or buy a product. Once you have determined actions you want to reward, you now must assign these actions relative scores/importances. For example, maybe reading your site's mission statement is worth 5 points, and buying a product is worth 10. This allows you to make some actions more important than others.


You can store this information in the Rewards class, in a map called "rewards". This is just an easy way to keep reward values for actions in one place. Rewards are identified by a string ID. For example, a user clicking the contact button on my website awards the site 5 points. In the rewards map, "CONTACT" is mapped to the number 5.0. 


The Rewards class also stores two other critical pieces of information, the number of website versions you are testing and a "redirects" map. The numVersions field holds the number of site versions you are currently testing. In my website, I only have two versions, so I set numVersions to 2. The redirects map is also something you will need to edit, and I will explain that next. 


There are two types of actions in this framework, actions that require a redirect, and actions that don't. For example, you may want to reward the site for a user following a link to a certain html page. To do this, instead of linking directly to the html page, you can link to the Rewarder servlet, which will subsequently redirect the user to your html page. This is where the Rewards "redirects" map comes in. For the Rewarder servlet to know where to send the user after they click a link you want to reward, you must map the reward id string to a html filename string, similarly to how we kept track of reward. For example, my "CONTACT" reward needs to redirect to contact.html, so in the redirects map, I map "CONTACT" to "contact". 

### The Rewarder Class
The Rewards class just held information about what rewards we have, how much they are worth, where they redirect, and how many site versions we have. The Rewarder class does all the work. Luckily, you don't have to touch anything here! Just by editing the Rewards class, the Rewarder knows how to use this information to reward your website versions for soliciting actions from users. 

### Putting Reward Actions on Your Page
As mentioned before, there are two types of actions, those that redirect and those that don't. Putting actions on your webpage is different if you are linking directly to the Rewarder servlet or running the Rewarder servlet in the background with XMLHttpRequest.
