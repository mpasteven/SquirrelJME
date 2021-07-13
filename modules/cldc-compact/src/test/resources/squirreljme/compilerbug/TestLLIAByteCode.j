; -*- Mode: Jasmin; indent-tabs-mode: t; tab-width: 4 -*-
; ---------------------------------------------------------------------------
; Multi-Phasic Applications: SquirrelJME
;     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
; ---------------------------------------------------------------------------
; SquirrelJME is under the GNU General Public License v3+, or later.
; See license.mkd for licensing and copyright information.
; ---------------------------------------------------------------------------
; There is a SummerCoat compiler bug with __LinkedListListIterator__
; Where DUP_X1 causes an object reference to be trashed

.class public squirreljme/compilerbug/TestLLIAByteCode

; Jasmin is being very impossible right now and I cannot declare fields in
; this class at all because of a stupid syntax error.......
.super squirreljme/compilerbug/__LLIAFields__
;.super net/multiphasicapps/tac/TestRunnable
;.field _vdx I
;.field list Lsquirreljme/compilerbug/FakeLinkedList;
;.field _next Lsquirreljme/compilerbug/__Link__;

.method public <init>()V
	aload 0
	;invokenonvirtual net/multiphasicapps/tac/TestRunnable/<init>()V
	invokenonvirtual squirreljme/compilerbug/__LLIAFields__/<init>()V
	return
.end method

; The actual test
.method public test()V
.limit stack 8
.limit locals 2
	; Setup Fake Linked List
	aload_0
	new squirreljme/compilerbug/FakeLinkedList
	dup
	astore_1
	dup
	invokenonvirtual squirreljme/compilerbug/FakeLinkedList/<init>()V
	putfield squirreljme/compilerbug/TestLLIAByteCode/list Lsquirreljme/compilerbug/FakeLinkedList;
	
	; Our next is the head of the list
	aload_0
	aload_1
	getfield squirreljme/compilerbug/FakeLinkedList/_head Lsquirreljme/compilerbug/__Link__;
	putfield squirreljme/compilerbug/TestLLIAByteCode/_next Lsquirreljme/compilerbug/__Link__;
	
	; Make a new fake object and add to the list
	aload_0
	new java/lang/Object
	dup
	invokenonvirtual java/lang/Object/<init>()V
	invokevirtual squirreljme/compilerbug/TestLLIAByteCode/add(Ljava/lang/Object;)V
	
	; VDX Value
	aload_0
	ldc "vdx"
	aload_0
	getfield squirreljme/compilerbug/TestLLIAByteCode/_vdx I
	invokevirtual net/multiphasicapps/tac/TestRunnable/secondary(Ljava/lang/String;I)V
	
	; atmod Value
	aload_0
	ldc "atmod"
	aload_0
	getfield squirreljme/compilerbug/TestLLIAByteCode/_atmod I
	invokevirtual net/multiphasicapps/tac/TestRunnable/secondary(Ljava/lang/String;I)V
	
	; Next link
	aload_0
	ldc "next"
	aload_0
	getfield squirreljme/compilerbug/TestLLIAByteCode/_next Lsquirreljme/compilerbug/__Link__;
	invokevirtual java/lang/Object/hashCode()I
	invokevirtual net/multiphasicapps/tac/TestRunnable/secondary(Ljava/lang/String;I)V
notnull:
	
	return
.end method

; This method is an issue that happens in the compiler, as commented below
.method public add(Ljava/lang/Object;)V
.limit stack 5
.limit locals 4
.line 94
	; #8 = this
	; #9 = arg1
	; #12 = stack...
	
	; [] -> [this]
	aload 0
	; [this] -> []
	invokenonvirtual squirreljme/compilerbug/TestLLIAByteCode/__checkConcurrent()V
	
.line 97
	; [] -> [this]
	aload 0
	; [this] -> [int this._vdx]
	getfield squirreljme/compilerbug/TestLLIAByteCode/_vdx I
	; [int this._vdx] -> []
	; #2 = int this._vdx
	istore 2
	
.line 98
	; [] -> [this]
	aload 0
	; [this] -> [__Link__ this._next]
	getfield squirreljme/compilerbug/TestLLIAByteCode/_next Lsquirreljme/compilerbug/__Link__;
	; [__Link__ this._next] -> []
	; #3 = __Link__ this._next
	astore 3
	
.line 105
	; [] -> [__Link__ a]
	new squirreljme/compilerbug/__Link__
	; [__Link__ a] -> [__Link__ a, __Link__ a]
	dup
	; [__Link__ a, __Link__ a] ->
	;     [__Link__ a, __Link__ a, __Link__ this._next (#3)]
	aload 3
	; [__Link__ a, __Link__ a, __Link__ this._next (#3)] ->
	;     [__Link__ a, __Link__ a.prev]
	getfield squirreljme/compilerbug/__Link__/_prev Lsquirreljme/compilerbug/__Link__;
	; [__Link__ a, __Link__ a.prev] ->
	;     [__Link__ a, __Link__ a.prev, Object arg1]
	aload 1
	; [__Link__ a,__Link__ a.prev, Object arg1] ->
	;     [__Link__ a, __Link__ a.prev, Object arg1, __Link__ this._next (#3)]
	aload 3
	; [__Link__ a, __Link__ a.prev, Object arg1, __Link__ this._next (#3)] ->
	;     [__Link__ a]
	invokenonvirtual squirreljme/compilerbug/__Link__/<init>(Lsquirreljme/compilerbug/__Link__;Ljava/lang/Object;Lsquirreljme/compilerbug/__Link__;)V
	; [__Link__ a] -> []
	pop
	
.line 108
	; [] -> [this]
	aload 0
	; [this] -> [this, this]
	dup
	; [this, this] -> [this, int this._vdx]
	getfield squirreljme/compilerbug/TestLLIAByteCode/_vdx I
	; [this, int this._vdx] -> [this, int this._vdx, 1]
	iconst_1
	; [this, int this._vdx, 1] -> [this, int this._vdx + 1]
	iadd
	; [this, int this._vdx + 1] -> []
	putfield squirreljme/compilerbug/TestLLIAByteCode/_vdx I
	
.line 111
	; [] -> [this]
	aload 0
	; [this] -> [FakeLinkedList this.list]
	getfield squirreljme/compilerbug/TestLLIAByteCode/list Lsquirreljme/compilerbug/FakeLinkedList;
	; [FakeLinkedList this.list] ->
	;     [FakeLinkedList this.list, FakeLinkedList this.list]
	dup
	; [FakeLinkedList this.list, FakeLinkedList this.list] ->
	;     [FakeLinkedList this.list, int this.list._size]
	getfield squirreljme/compilerbug/FakeLinkedList/_size I
	; [FakeLinkedList this.list, int this.list._size] ->
	;     [FakeLinkedList this.list, int this.list._size, 1]
	iconst_1
	; [FakeLinkedList this.list, int this.list._size, 1] ->
	;     [FakeLinkedList this.list, int this.list._size + 1]
	iadd
	; [FakeLinkedList this.list, int this.list._size + 1] -> []
	putfield squirreljme/compilerbug/FakeLinkedList/_size I
	
.line 114
	; >> "*** Java :114 ALOAD_0@51 ***"
	; >> "*** Java :114 ALOAD_0@52 ***"
	; [] -> [this]
	aload 0
	; [this] -> [this, this]
	aload 0
	
	; >> "*** Java :114 GETFIELD@53 ***"
	; r13 == LinkedList
	; >> "IF_ICMP_EQUALS:[[8, 0, 1376]]"
	; >> "LOAD_POOL:[[#5658(ACCESSED_FIELD):READ+INSTANCE+field
	;    java/util/__LinkedListListIterator__::list Ljava/util/LinkedList;,
	;    20]]"
	; >> "MEM_HANDLE_OFF_LOAD_OBJECT_REG_JAVA:[[13, 8, 20]]"
	; >> "IF_ICMP_EQUALS:[[13, 0, 7]]"
	; >> "MEM_HANDLE_COUNT_UP:[[13]]"
	; [this, this] -> [this, FakeLinkedList list]
	getfield squirreljme/compilerbug/TestLLIAByteCode/list Lsquirreljme/compilerbug/FakeLinkedList;
	
	; >> "*** Java :114 DUP@56 ***"
	; [this, FakeLinkedList list] -> [this, FakeLinkedList list, FakeLinkedList list]
	dup
	
	; >> "*** Java :114 GETFIELD@57 ***"
	; >> "IF_ICMP_EQUALS:[[13, 0, 1348]]"
	; >> "LOAD_POOL:[[#5271(ACCESSED_FIELD):READ+INSTANCE+field
	;    java/util/LinkedList::modCount I, 20]]"
	; >> "MEM_HANDLE_OFF_LOAD_INTEGER_REG_JAVA:[[14, 13, 20]]"
	; [this, FakeLinkedList list, FakeLinkedList list] ->
	;     [this, FakeLinkedList list, int list.modCount]
	getfield squirreljme/compilerbug/FakeLinkedList/modCount I
	
	; >> "*** Java :114 ICONST_1@60 ***"
	; >> "INTEGER_OR_CONST:[[0, 1, 15]]"
	; [this, FakeLinkedList list, int list.modCount] ->
	;     [this, FakeLinkedList list, int list.modCount, 1]
	iconst_1
	
	; >> "*** Java :114 IADD@61 ***"
	; >> "INTEGER_ADD_REG:[[14, 15, 14]]"
	;        #13                  #14                #15
	; [this, FakeLinkedList list, int list.modCount, 1] ->
	;     [this, FakeLinkedList list, int list.modCount + 1]
	iadd
	
	; >> "*** Java :114 DUP_X1@62 ***"
	; >> "COPY:[[14, 18]]"
	; r13 (our linked list) gets trashed here with the method
	; >> "COPY:[[18, 13]]"
	;        #13                  #14->#18                         #18->#13
	; [this, FakeLinkedList list, int list.modCount + 1] ->
	;     [this, int list.modCount + 1, FakeLinkedList list,
	;      int list.modCount + 1]
	; DB: ********************
	; DB: Preprocessing STACKSHUFFLE:[DUP_X1] (@62#DUP_X1~:[])
	; DB: Shuffle with: DUP_X1 -> [[ba] -> [aba]]
	; DB: Popped: [r13:Ljava/util/LinkedList;, r14:I]
	; DB: Source map: {1=r13:Ljava/util/LinkedList;, 0=r14:I}
	; DB: Collide origUseVal=-1 ssl.value=14 useVal=14 ods.register=13
	; DB: Pre 18 -> 13
	; DB: Pre-copies: {14=18}
	; DB: *** Stack Result ***
	; DB: BEF: State:{L=[r8:Ljava/util/__LinkedListListIterator__;:RN,
	;     r9:Ljava/lang/Object;:RN, r10:I, r11:Ljava/util/__Link__;],
	;     S=[r8(12):Ljava/util/__LinkedListListIterator__;:N,
	;     r13:Ljava/util/LinkedList;, r14:I]}
	; DB: AFT: State:{L=[r8:Ljava/util/__LinkedListListIterator__;:RN,
	;     r9:Ljava/lang/Object;:RN, r10:I, r11:Ljava/util/__Link__;],
	;     S=[r8(12):Ljava/util/__LinkedListListIterator__;:N, r13:I,
	;     r13(14):Ljava/util/LinkedList;:N, r13(15):I]}
	;     ^^^^^ ALL THREE OF THESE ARE r13??? THIS IS NOT GOOD!
	; DB: IN : []
	; DB: OUT: []
	; DB: OPS: [COPY(14, 18), COPY(18, 13)]
	dup_x1
	
	; >> "*** Java :114 PUTFIELD@63 ***"
	; >> "IF_ICMP_EQUALS:[[13, 0, 1155]]"
	; >> "LOAD_POOL:[[#5272(ACCESSED_FIELD):NORMAL+INSTANCE+field
	;    java/util/LinkedList::modCount I, 20]]"
	; >> "MEM_HANDLE_OFF_STORE_INTEGER_REG_JAVA:[[13, 13, 20]]"
	;                                             ^ should be 14 (for list)!
	; >> "IF_ICMP_NOT_EQUALS:[[3, 0, 1579]]"
	; [this, int list.modCount + 1, FakeLinkedList list,
	;     int list.modCount + 1] -> [this, int list.modCount + 1]
	putfield squirreljme/compilerbug/FakeLinkedList/modCount I
	
	; >> "IF_ICMP_EQUALS:[[8, 0, 1133]]"
	; >> "LOAD_POOL:[[#5659(ACCESSED_FIELD):NORMAL+INSTANCE+field
	;    java/util/__LinkedListListIterator__::_atmod I, 20]]"
	; >> "MEM_HANDLE_OFF_STORE_INTEGER_REG_JAVA:[[13, 8, 20]]"
	; >> "IF_ICMP_NOT_EQUALS:[[3, 0, 1557]]"
	; [this, int list.modCount + 1] -> []
	putfield squirreljme/compilerbug/TestLLIAByteCode/_atmod I
	
.line 117
	aload 0
	aconst_null
	putfield squirreljme/compilerbug/TestLLIAByteCode/_last Lsquirreljme/compilerbug/__Link__;
	
.line 118
	return
.end method

; Does nothing here for this test
.method __checkConcurrent()V
	return
.end method
