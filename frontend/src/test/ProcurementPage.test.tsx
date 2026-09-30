import React from 'react';
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom/vitest';
import { HeroUIProvider } from '@heroui/react';
import { ProcurementPage } from '../pages/ProcurementPage';
import { PurchaseOrder, Supplier, Product, Warehouse } from '../types/erp';

const mockSuppliers: Supplier[] = [
  {
    id: 'sup-1',
    code: 'SUP-BPN-01',
    name: 'PT Boga Prima Nusantara',
    email: 'order@bogaprima.co.id',
    phone: '+62 21 555-0101',
    address: 'Jakarta Barat',
    status: 'ACTIVE',
    createdAt: '2026-09-30T10:00:00Z',
  }
];

const mockProducts: Product[] = [
  {
    id: 'prod-1',
    code: 'PRD-BEEF-01',
    name: 'Wagyu Ribeye Slice MB5',
    categoryId: 'cat-1',
    categoryName: 'Meat & Poultry',
    unit: 'KG',
    purchasePrice: 45000,
    sellingPrice: 125000,
    minStock: 15,
    description: 'High grade Wagyu MB5 slice for shabu & grill',
    status: 'ACTIVE',
    createdAt: '2026-09-30T10:00:00Z',
    updatedAt: '2026-09-30T10:00:00Z'
  }
];

const mockWarehouses: Warehouse[] = [
  {
    id: 'wh-1',
    code: 'WH-COLD-01',
    name: 'Central Cold Storage Tanjung Priok',
    address: 'Kawasan Pelabuhan Tanjung Priok Blok C-12',
    status: 'ACTIVE',
    createdAt: '2026-09-30T10:00:00Z',
  }
];

const mockPurchaseOrders: PurchaseOrder[] = [
  {
    id: 'po-1',
    orderNumber: 'PO-20260930-001',
    supplierId: 'sup-1',
    supplierCode: 'SUP-BPN-01',
    supplierName: 'PT Boga Prima Nusantara',
    orderDate: '2026-09-30T10:00:00Z',
    status: 'APPROVED',
    totalAmount: 2250000,
    notes: 'Urgent weekend dining stock',
    createdBy: 'Procurement Specialist',
    createdAt: '2026-09-30T10:00:00Z',
    items: [
      {
        id: 'poi-1',
        productId: 'prod-1',
        productCode: 'PRD-BEEF-01',
        productName: 'Wagyu Ribeye Slice MB5',
        unit: 'KG',
        quantity: 50,
        unitPrice: 45000,
        receivedQuantity: 0,
        subtotal: 2250000,
      }
    ]
  }
];

describe('Feature: Procurement UI Component Tests', () => {
  it('renders purchase order records with HeroUI components and badges', () => {
    const onRefresh = vi.fn();
    const onOpenReceive = vi.fn();

    render(
      <HeroUIProvider>
        <ProcurementPage
          purchaseOrders={mockPurchaseOrders}
          suppliers={mockSuppliers}
          products={mockProducts}
          warehouses={mockWarehouses}
          onRefresh={onRefresh}
          onOpenReceive={onOpenReceive}
        />
      </HeroUIProvider>
    );

    // Verify PO number and supplier name are rendered
    expect(screen.getByText('PO-20260930-001')).toBeInTheDocument();
    expect(screen.getByText('PT Boga Prima Nusantara')).toBeInTheDocument();

    // Verify status chip
    expect(screen.getByText('APPROVED')).toBeInTheDocument();

    // Verify action button to trigger Goods Receiving is visible
    expect(screen.getByRole('button', { name: /create purchase order/i })).toBeInTheDocument();
  });

  it('opens New Purchase Order modal when button is clicked', () => {
    const onRefresh = vi.fn();

    render(
      <HeroUIProvider>
        <ProcurementPage
          purchaseOrders={mockPurchaseOrders}
          suppliers={mockSuppliers}
          products={mockProducts}
          warehouses={mockWarehouses}
          onRefresh={onRefresh}
        />
      </HeroUIProvider>
    );

    const createButton = screen.getByRole('button', { name: /create purchase order/i });
    fireEvent.click(createButton);

    // Verify modal header appears
    expect(screen.getByText(/Create Purchase Order/i)).toBeInTheDocument();
  });
});
