; -*- Mode: Jasmin; indent-tabs-mode: t; tab-width: 4 -*-
; ---------------------------------------------------------------------------
; Multi-Phasic Applications: SquirrelJME
;     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
; ---------------------------------------------------------------------------
; SquirrelJME is under the GNU General Public License v3+, or later.
; See license.mkd for licensing and copyright information.
; ---------------------------------------------------------------------------

.class public lang/interfaces/TestInvokeVirtualOnAbstractImplInterface
.super net/multiphasicapps/tac/TestInteger

.method public <init>()V
	aload 0
	invokenonvirtual net/multiphasicapps/tac/TestInteger/<init>()V
	return
.end method

.method public test()I
.limit stack 3
	; Allocate and initialize an implementation
	new lang/interfaces/ImplAbstractImplAWithMethod
	dup
	invokespecial lang/interfaces/ImplAbstractImplAWithMethod/<init>()V
	
	; Duplicate for the second and third call
	dup
	dup
	
	; Call the first virtually, it should still be valid
	invokevirtual lang/interfaces/AbstractImplANoMethod/methodA()I
	
	; Then call it on the sub-class
	swap
	invokevirtual lang/interfaces/ImplAbstractImplAWithMethod/methodA()I
	
	; Add both together
	iadd
	
	; Call via interface next
	swap
	invokeinterface lang/interfaces/InterfaceA/methodA()I 1
	
	; Add both values together which becomes the result
	iadd
	ireturn
.end method
