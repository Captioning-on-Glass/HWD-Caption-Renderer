# cog-ip-glasses

This is the companion Android app to the Group Captioning Experiment video display found at https://github.com/Captioning-on-Glass/Group-Conversation-Simulation.  It is not meant to be used out of an experimental context.

The Group Captioning Experiment consists of two parts.  One is a C app running a captioned video on a monitor, and the other is an Android app running on some headworn display(eg. Google Glass EE2).  The main idea is that the headworn display transmits the participant's head azimuth position to the C app over Wireless, and the C app uses the information to determine where the participant is facing, and moves the caption on the video into the user's line of sight.

