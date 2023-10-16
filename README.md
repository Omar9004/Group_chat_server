# Group_chat_server
Group_chat_server using Threads 

I have developed a concurrent server / client for an Internet multi-party chat service (like IRC) using the TCP protocol. Users run the client program, which connects to the server. Then users can send typed-in user messages to that server which then distributes them to the other clients. Incoming messages can be distributed either directly (immediately) or alternatively the server can only inform the other clients about fresh messages and their origin. This way the client can decide whether to fetch the new message(s) or not. The server can also inform all connected parties when one of the users loses the connection or logs out.

Features:
1. Multiple clients can join the group chat
2. The first client who connects to the server is the "Super user" who can remove anyone from the group. By typing "rmv" to server.
3. A client can send a direct message (DM) to another client privately by typing "@" and then the name of the client.
4. When one client leaves the group all other clients who are still connected to the group will be informed about the client's exit.
