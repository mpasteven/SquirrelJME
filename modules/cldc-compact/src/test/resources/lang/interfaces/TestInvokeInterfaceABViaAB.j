; -*- Mode: Jasmin; indent-tabs-mode: t; tab-width: 4 -*-
; ---------------------------------------------------------------------------
; Multi-Phasic Applications: SquirrelJME
;     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
; ---------------------------------------------------------------------------
; SquirrelJME is under the GNU General Public License v3+, or later.
; See license.mkd for licensing and copyright information.
; ---------------------------------------------------------------------------
; Same as TestInvokeInterfaceAB except it invokes the interface methods via
; InterfaceAB and not InterfaceA+InterfaceB.

.class public lang/interfaces/TestInvokeInterfaceABViaAB
.super net/multiphasicapps/tac/TestInteger

.method public <init>()V
	aload 0
	invokenonvirtual net/multiphasicapps/tac/TestInteger/<init>()V
	return
.end method

.method public test()I
.limit stack 2
	; Allocate and initialize an implementation
	new lang/interfaces/ImplementsAB
	dup
	invokespecial lang/interfaces/ImplementsAB/<init>()V
	
	; Duplicate for the second call
	dup
	
	; Call the first, the result is on the stack
	invokeinterface lang/interfaces/InterfaceAB/methodA()I 1
	
	; Swap the two so the object is at the top then call again
	swap
	invokeinterface lang/interfaces/InterfaceAB/methodB()I 1
	
	; Add both values together which becomes the result
	iadd
	ireturn
.end method

