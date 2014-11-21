Description of Steganography com.steganomobile.lib.Methods used in application
====================================================================================================

## Volume Settings
Sequence of data is encoded into level of volume. With Music channel there is possibility to send
four bits per iteration. Other channels have eight levels of volume, so we could send three bits per
one iteration.

## Type of Intent
Sender registers some intent actions and receiver sends data with appropriate action.

## Unix Socket Discovery
Sending data through state of socket. Closed socket is equal to one, opened to zero. Closed socket
is detected with Exceptions.

## File Lock
Sender sends one by locking the file. Receiver also is trying to lock file. If he succeeded, he
interprets action as zero. If he get en exception, it means that Sender has locked file before
Receiver attempt. Action equals to one.|

## File Existence
Similar to File Lock. In this case we are checking existence of shared file.

## File Size
Sender sets size of shared file and Receiver interprets it as a byte.

## Memory Load
Receiver gets initial memory load of Sender. Then Sender allocates data, and Receiver compares
current memory load to initial state. Currently not working, cause GC does not deallocate memory,
if we want to allocate less memory than we allocate in previous iteration.

## System Load
Sender sends one by doing a lot of actions on CPU. Receiver check how many clock ticks Sender has
got since previous iteration. If the usage is greater than border usage he interprets it as one.
In the other case he interprets action as zero. Use time interval >= 200 [ms].

## Usage Trend
Similar to System Load, but we are checking trend of measured usage. If usage increases - one.
In other case - zero. Use time interval >= 200 [ms]

*Method details*

| Method                | Article   | Bit rate [b/s] | Synchronous | Detected | ?-Based  |
| --------------------- | :-------: | :------------: | :---------: | :------: | :------: |
| Volume Settings       | 1, 2      | 450            | Yes         | No       | Software |
| Type of Intent        | 1, 3      | 15000          | Yes         | No       | Software |
| Unix Socket Discovery | 1, 2, 3   | 100            | Yes         | No       | Software |
| File Lock             | 2, 3      | 250            | Yes         | No       | Software |
| File Existence        | *         | 250            | Yes         | No       | Software |
| File Size             | *         | 2000           | Yes         | No       | Software |
| Memory Load           | *         | 500            | Yes         | No       | Hardware |
| System Load           | 1, 3      | 5              | Yes         | No       | Hardware |
| Usage Trend           | 1         | 5              | Yes         | No       | Hardware |

References:
----------------------------------------------------------------------------------------------------
[*] Method not used in previous papers

[1] **Analysis of the communication between colluding applications on modern smartphones**
    C. Marforio, H. Ritzdorf, A. Francillon, S. Capkun
    Proceedings of the 28th Annual Computer Security Applications Conference, 51-60

[2] **Soundcomber: A Stealthy and Context- Aware Sound Trojan for Smartphones"**
    R. Schlegel, K. Zhang, X. Zhou, M. Intwala, A. Kapadia, and X.
    Wang, , in Proceedings of Network and Distributed System
    Security Symposium, San Diego, USA: The Internet Society, Feb. 2011.

[3] **Hiding Privacy Leaks in Android Applications Using Low-Attention Raising Covert Channels**
    J.-F. Lalande, S. Wendzel