; -*- Mode: Jasmin; indent-tabs-mode: t; tab-width: 4 -*-
; ---------------------------------------------------------------------------
; Multi-Phasic Applications: SquirrelJME
;     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
; ---------------------------------------------------------------------------
; SquirrelJME is under the GNU General Public License v3+, or later.
; See license.mkd for licensing and copyright information.
; ---------------------------------------------------------------------------

.class public lang/interfaces/TestInvokeInterfaceSubClassNoReplace
.super net/multiphasicapps/tac/TestInteger

.method public <init>()V
	aload 0
	invokenonvirtual net/multiphasicapps/tac/TestInteger/<init>()V
	return
.end method

.method public test()I
.limit stack 2
	; Allocate and initialize an implementation
	new lang/interfaces/SubClassImplANoReplace
	dup
	invokespecial lang/interfaces/SubClassImplANoReplace/<init>()V
	
	invokeinterface lang/interfaces/InterfaceA/methodA()I 1
	ireturn
.end method
	
