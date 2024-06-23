#include "DEV_Config.h"
#include "LCD_1in28.h"
#include "GUI_Paint.h"
#include "GUI_BMP.h"
#include "test.h"
#include "image.h"
#include <stdio.h>		//printf()
#include <stdlib.h>		//exit()
#include <signal.h>     //signal()

#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <pthread.h>
#include <fcntl.h> 
#include <sys/ioctl.h> 
#include <linux/types.h> 
#include <linux/spi/spidev.h>


#define SIZE_BMP 230538
//BitMaps welche ausgegeben werden. Diese werden bei jedem neuem Gemütszustand ersetzt
#define PATH_BMP "./bmps/b0.bmp"

typedef struct sockaddr sockaddr_t;
typedef struct sockaddr_in sockaddr_in_t;

typedef struct
{
	sockaddr_in_t address;
	unsigned int lengthAddress;
	int socketDescriptor;
}client;

//Thread Funktion, um neue BitMaps zu empfangen
//Enthält einen TCP-Server
void* receiverPictures(void* args);

int main(int argc, char *argv[])
{
    // Exception handling:ctrl + c
    signal(SIGINT, Handler_1IN28_LCD);
    
    /* Module Init */
	if(DEV_ModuleInit() != 0){
        DEV_ModuleExit();
        exit(0);
    }
	
    //SEHR WICHTIG, da die WaveShare API den Pin falsch initialisiert
    DEV_GPIO_Mode(18, BCM2835_GPIO_FSEL_OUTP);
    
    //Starten des TCP-Server Threads zum empfangen der BitMaps
    pthread_t pictureReceiver = 0;
    int* picturesAreSwapped = malloc(sizeof(int));
    int errorThreadCreate = pthread_create(&pictureReceiver, NULL, receiverPictures, picturesAreSwapped);
    if(errorThreadCreate == -1){perror("Error creating thread to server client");}
    
    /* LCD Init */
	printf("1.28inch LCD demo...\r\n");
	LCD_1IN28_Init(HORIZONTAL);
	LCD_1IN28_Clear(BLACK);
	LCD_SetBacklight(1023);
    LCD_1IN28_BL_1;
	
    UWORD *BlackImage;
    UDOUBLE Imagesize = LCD_1IN28_HEIGHT*LCD_1IN28_WIDTH*2;
    printf("Imagesize = %d\r\n", Imagesize);
    if((BlackImage = (UWORD *)malloc(Imagesize)) == NULL) {
        printf("Failed to apply for black memory...\r\n");
        exit(0);
    }
    //Create a new image cache named IMAGE_RGB and fill it with white
    Paint_NewImage(BlackImage, LCD_1IN28_WIDTH, LCD_1IN28_HEIGHT, 0, BLACK, 16);
    Paint_Clear(BLACK);
	Paint_SetRotate(ROTATE_0);
    
    //Loop zum Anzeigen der BitMaps auf dem Bildschirm 
    char pathCurrentBmp[] = PATH_BMP;
    while(1)
    {
        //Warten bis alle BitMaps angekommen sind
        while(*picturesAreSwapped == 0){}
        for(int i = 0; i < *picturesAreSwapped; i++)
        {
            pathCurrentBmp[strlen(pathCurrentBmp) - 5] = i + 0x30;
            //zum warten Springen, falls neue BitMaps da sind
            if(*picturesAreSwapped == 0){break;}
            printf("bmp: %s\n", pathCurrentBmp);
            GUI_ReadBmp(pathCurrentBmp);
            DEV_Delay_ms(1000);
            LCD_1IN28_Display(BlackImage);
        }
        DEV_Delay_ms(4000);
    }  
    
    /* Module Exit */
    LCD_1IN28_BL_0;
    free(BlackImage);
    BlackImage = NULL;
	DEV_ModuleExit();
}
void* receiverPictures(void* args)
{
    //Server Init 
    int socketDescriptor = socket(AF_INET, SOCK_STREAM, 0);
	if(socketDescriptor == -1) {perror("Error getting socket: ");}
	printf("Got socket\n");
	sockaddr_in_t serverAddress;
	serverAddress.sin_family = AF_INET;
	serverAddress.sin_addr.s_addr = inet_addr("192.168.2.112");
	serverAddress.sin_port = htons(42420);
	printf("I am: %s:%d\n", inet_ntoa(serverAddress.sin_addr), ntohs(serverAddress.sin_port));
	
	int bindSuccessfull = bind(socketDescriptor, (sockaddr_t*)&serverAddress, sizeof(serverAddress));
	if(bindSuccessfull == -1) {perror("Error binding socket: ");}
	printf("bind successfull\n");
	
	int listenSuccessfull = listen(socketDescriptor, 5);
	if(listenSuccessfull == -1) {perror("Error listening on socket: ");}
	printf("listen successfull\n");
	
    //Loop tum azkeptieren von einem Client und zum Empfangen von den BitMaps 
	while(1)
	{
		client currentClient;
		currentClient.lengthAddress = sizeof(currentClient.address);
        currentClient.socketDescriptor = accept(socketDescriptor, (sockaddr_t*)&serverAddress, &currentClient.lengthAddress);
		if(currentClient.socketDescriptor == -1) {perror("Error accepting client: ");}
		else
		{
			printf("---------------------------\n");
			printf("clientAccepted\n");
            *(int*)args = 0;
            
            //Empfangen der Anzahl der BitMaps
            int BmpCount = 0;
            int lengthOfBmpCountReaceived = 0;
            while(lengthOfBmpCountReaceived < sizeof(int))
            {
                int temp = recv(currentClient.socketDescriptor, (void*)&BmpCount + lengthOfBmpCountReaceived, sizeof(int) - lengthOfBmpCountReaceived, 0);
                if(temp == -1) {perror("Error receiving data size from client: ");}
                if(temp == 0)
                {
                    printf("Client FORECE disconnected\n");
                    printf("---------------------------\n");
                    return 0;
                }
                lengthOfBmpCountReaceived += temp;
                printf("BmpCount - %6d/%6d\n", lengthOfBmpCountReaceived, sizeof(int));
            }
            printf("BmpCount: %d\n", BmpCount);
            
            //Das eigentliche Empfangen der BitMaps
            char pathCurrentBmp[] = PATH_BMP;
            for(int i = 0; i < BmpCount; i++)
            {
                pathCurrentBmp[strlen(pathCurrentBmp) - 5] = i + 0x30;
                int fd = open(pathCurrentBmp, O_RDWR | O_CREAT | O_TRUNC, S_IRWXU | S_IRWXG | S_IRWXO);
                if(fd == -1){perror("error opening file"); exit(-1);}
                char buffer[SIZE_BMP] = {0};
                int sizeOfBmpGot = 0;
                while(sizeOfBmpGot < SIZE_BMP)
                {
                    int lenghtOfDataReceived = 0;
                    lenghtOfDataReceived = recv(currentClient.socketDescriptor, buffer + sizeOfBmpGot, SIZE_BMP - sizeOfBmpGot, 0);
                    if(lenghtOfDataReceived == -1){perror("Error getting bmp"); exit(-1);}
                    printf("%4d - %6d/%6d\n", i, sizeOfBmpGot, SIZE_BMP);
                    sizeOfBmpGot += lenghtOfDataReceived;
                }
                //Schreiben der BitMaps auf die SD-Karte
                int sizeDataWritten = 0;
                while(sizeDataWritten < SIZE_BMP)
                {
                    int temp = write(fd, buffer + sizeDataWritten, 256);
                    if(temp == -1){perror("Error writing bmp"); exit(-1);}
                    sizeDataWritten += temp;
                    printf("W%3d - %6d/%6d\n", i, sizeDataWritten, SIZE_BMP);
                }
                close(fd);
            }
            *(int*)args = BmpCount;
		}		
	}
    
}
