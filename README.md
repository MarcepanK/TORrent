TORrent
===
#### How to launch
###### tracker
In order to launch any client, tracked has to be up and running. 
To run Tracker go to scripts directory and exec tracker.sh script.</br><br>
To exit tracker just press ctrl+c.
###### client
To launch client go to scripts directory and exec client.sh script.
After launching, type in clients id (we assume that id > 0). After that you
should receive 'hello' message from tracker. If 'hello' shows up in terminal,
client is ready to work.</br>
<br>
To exit type in 'request disconnect'. After exiting with ctrl+c, client doesn't 
send disconnect request to tracker which will try to track this 'peer' and some 
undefined behavior might happen.
___
#### Commands available for client
* request pull <file_name> - download file from multiple clients at once
* request push <client_id> <file_name> - send file to client with $client_id
* request files - get list of files that are available to download
* request disconnect - disconnects from tracker and quits program
* continue <file_name> - download parts of file that are missing due to some error - 
Something is not working here (it downloads whole file again)
* list broken - list files that are not complete due to some error
* list files - list owned files
___
#### Communication between hosts explanation
Every running client is connected to tracker which is main communication hub.
After executing any command starting with 'request, serialized object is sent to tracker.
<br> Tracker after receiving request processes it (i.e. sends list of files to client or in case
of file transfer requests(PushRequest, PullRequest) generates Orders that will be sent
to all clients taking part in transfer).
###### Types of Requests:
* PushRequest - contains:
    * requester id - id of client that will upload file
    * request code
    * destination host id - id of client that will download file
    * file name
* PullRequest - contains:
    * requester id - id of client that will download file
    * request code
    * file name
* UpdateRequest - contains:
    * requester id
    * request code
    * downloaded - bytes downloaded
    * uploaded - bytes uploaded
    * file name
 * RetryDownloadRequest - contains(not working yet):
    * Metadata of file that will be transferred
    * missing pieces indexes
    * missing trailing bytes
 * Request - contains:
    * requester id
    * request code
    
    Request might be used with codes(DISCONNECT or FILE_LIST) 
    since these two don't require any additional parameters.
    
 ###### Types of orders:
 * DownloadOrder - contains:
    * Metadata of file that will be transferred(file name, size, md5sum)
    * Metadata of seeds (id, address) 
 * UploadOrder - contains:
    * leech id - id of client that will download file
    * file part to send - number of part that client will send
    * total parts - number of parts transferred file will be split to
 * SpecificPiecesUploadOrder - contains (not working yet):
    * leech id
    * collection of piece indexes that will be sent to leech
    * trailing bytes to send - if all indexes are correct (there are 
    no pieces missing in the middle of the file) but length of all pieces combined is less than file size
    then seed will need to send 'trailing bytes' 
    
  ###### Some images to visualise communication process:
   
___
##### TODO:
 * close client when he looses connection to tracker
 * images to visualise communication process
 * unit tests
 * fix mess with collection types and toArray conversions
 * fix mess with types (long, int) in FileUtils
 * implement sending updated list of owned files to tracker (i.e. in case of deleting file)
 * optimize getting specific pieces from file in FileUtils
 * checking with regex instead of startsWith in Client/CommandProcessor
 * getting specific pieces in Client/FileUtils
 * retrying downloading file after connection unexpectedly closes during file transfer
 * new FileTransferService for retrying downloading file in Client
 * Make factories do heavy logic while creating objects
 * (Optional) make tracker actually track all active transfers