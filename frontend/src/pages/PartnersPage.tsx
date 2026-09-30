import React, { useState } from 'react';
import { Users, Truck, Store, Plus, Edit2, Trash2, Search, Phone, Mail, MapPin } from 'lucide-react';
import {
  Tabs,
  Tab,
  Card,
  CardBody,
  Button,
  Input,
  Chip,
  Modal,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  useDisclosure,
  Textarea,
  Select,
  SelectItem,
} from '@heroui/react';
import { Supplier, Customer } from '../types/erp';
import { erpApi } from '../api/erpApi';

interface PartnersPageProps {
  suppliers: Supplier[];
  customers: Customer[];
  onRefresh: () => void;
}

export const PartnersPage: React.FC<PartnersPageProps> = ({
  suppliers,
  customers,
  onRefresh,
}) => {
  const [selectedTab, setSelectedTab] = useState<'suppliers' | 'customers'>('suppliers');
  const [search, setSearch] = useState('');
  const [editingItem, setEditingItem] = useState<Supplier | Customer | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { isOpen, onOpen, onOpenChange, onClose } = useDisclosure();

  const [formData, setFormData] = useState({
    code: '',
    name: '',
    email: '',
    phone: '',
    address: '',
    status: 'ACTIVE' as 'ACTIVE' | 'INACTIVE',
  });

  const handleOpenAdd = () => {
    setEditingItem(null);
    const prefix = selectedTab === 'suppliers' ? 'SUP' : 'CUST';
    setFormData({
      code: `${prefix}-${Math.floor(100 + Math.random() * 900)}`,
      name: '',
      email: '',
      phone: '',
      address: '',
      status: 'ACTIVE',
    });
    onOpen();
  };

  const handleOpenEdit = (item: Supplier | Customer) => {
    setEditingItem(item);
    setFormData({
      code: item.code,
      name: item.name,
      email: item.email || '',
      phone: item.phone || '',
      address: item.address || '',
      status: item.status,
    });
    onOpen();
  };

  const handleSubmit = async () => {
    if (!formData.name || !formData.code) {
      alert('Please fill code and name.');
      return;
    }
    setIsSubmitting(true);
    try {
      if (selectedTab === 'suppliers') {
        if (editingItem) {
          await erpApi.updateSupplier(editingItem.id, formData);
        } else {
          await erpApi.createSupplier(formData);
        }
      } else {
        if (editingItem) {
          await erpApi.updateCustomer(editingItem.id, formData);
        } else {
          await erpApi.createCustomer(formData);
        }
      }
      onClose();
      onRefresh();
    } catch (err: any) {
      alert('Error saving: ' + err.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id: string) => {
    const typeLabel = selectedTab === 'suppliers' ? 'supplier' : 'client outlet';
    if (!confirm(`Are you sure you want to delete this ${typeLabel}?`)) return;
    try {
      if (selectedTab === 'suppliers') {
        await erpApi.deleteSupplier(id);
      } else {
        await erpApi.deleteCustomer(id);
      }
      onRefresh();
    } catch (err: any) {
      alert('Error deleting: ' + err.message);
    }
  };

  const filteredSuppliers = suppliers.filter(
    (s) =>
      s.name.toLowerCase().includes(search.toLowerCase()) ||
      s.code.toLowerCase().includes(search.toLowerCase())
  );

  const filteredCustomers = customers.filter(
    (c) =>
      c.name.toLowerCase().includes(search.toLowerCase()) ||
      c.code.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-4">
        <Tabs
          selectedKey={selectedTab}
          onSelectionChange={(key) => setSelectedTab(key as any)}
          color="primary"
          variant="solid"
          size="sm"
        >
          <Tab
            key="suppliers"
            title={
              <div className="flex items-center gap-2">
                <Truck className="w-4 h-4" />
                <span>Suppliers ({suppliers.length})</span>
              </div>
            }
          />
          <Tab
            key="customers"
            title={
              <div className="flex items-center gap-2">
                <Store className="w-4 h-4" />
                <span>Restaurant Outlets ({customers.length})</span>
              </div>
            }
          />
        </Tabs>

        <div className="flex items-center gap-3">
          <Input
            placeholder={`Search ${selectedTab}...`}
            size="sm"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            startContent={<Search className="w-4 h-4 text-neutral-400" />}
            className="w-64"
            isClearable
            onClear={() => setSearch('')}
          />

          <Button
            color="primary"
            size="sm"
            className="font-semibold shadow-md shadow-blue-500/20"
            startContent={<Plus className="w-4 h-4" />}
            onClick={handleOpenAdd}
          >
            Add {selectedTab === 'suppliers' ? 'Supplier' : 'Outlet'}
          </Button>
        </div>
      </div>

      {/* Grid of Partners */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {(selectedTab === 'suppliers' ? filteredSuppliers : filteredCustomers).map((item) => (
          <Card
            key={item.id}
            className="border border-neutral-200 dark:border-neutral-800 bg-white dark:bg-neutral-900 shadow-sm hover:shadow-md transition-all"
          >
            <CardBody className="p-5 space-y-4">
              <div className="flex items-start justify-between">
                <div>
                  <span className="font-mono text-[11px] font-semibold text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-950/50 px-2 py-0.5 rounded">
                    {item.code}
                  </span>
                  <h3 className="font-bold text-sm text-neutral-900 dark:text-white mt-1">
                    {item.name}
                  </h3>
                </div>

                <div className="flex items-center gap-1">
                  <Chip
                    size="sm"
                    variant="flat"
                    color={item.status === 'ACTIVE' ? 'success' : 'default'}
                    className="text-[10px] h-5 font-semibold"
                  >
                    {item.status}
                  </Chip>
                  <Button
                    isIconOnly
                    size="sm"
                    variant="light"
                    onClick={() => handleOpenEdit(item)}
                    className="text-neutral-400 hover:text-blue-600"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </Button>
                  <Button
                    isIconOnly
                    size="sm"
                    variant="light"
                    onClick={() => handleDelete(item.id)}
                    className="text-neutral-400 hover:text-rose-600"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </Button>
                </div>
              </div>

              <div className="space-y-2 text-xs text-neutral-600 dark:text-neutral-400 pt-2 border-t border-neutral-100 dark:border-neutral-800">
                {item.email && (
                  <div className="flex items-center gap-2">
                    <Mail className="w-3.5 h-3.5 text-neutral-400 shrink-0" />
                    <span className="truncate">{item.email}</span>
                  </div>
                )}
                {item.phone && (
                  <div className="flex items-center gap-2">
                    <Phone className="w-3.5 h-3.5 text-neutral-400 shrink-0" />
                    <span>{item.phone}</span>
                  </div>
                )}
                {item.address && (
                  <div className="flex items-start gap-2">
                    <MapPin className="w-3.5 h-3.5 text-neutral-400 shrink-0 mt-0.5" />
                    <span className="line-clamp-2">{item.address}</span>
                  </div>
                )}
              </div>
            </CardBody>
          </Card>
        ))}
      </div>

      {/* Modal */}
      <Modal isOpen={isOpen} onOpenChange={onOpenChange}>
        <ModalContent>
          <ModalHeader>
            <h3 className="text-base font-bold">
              {editingItem ? 'Edit Details' : `Add New ${selectedTab === 'suppliers' ? 'Supplier' : 'Restaurant Outlet'}`}
            </h3>
          </ModalHeader>
          <ModalBody className="space-y-4">
            <Input
              label="Code"
              size="sm"
              value={formData.code}
              onChange={(e) => setFormData({ ...formData, code: e.target.value })}
              required
            />
            <Input
              label="Business / Organization Name"
              size="sm"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
              <Input
                label="Email"
                size="sm"
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              />
              <Input
                label="Phone"
                size="sm"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              />
            </div>
            <Textarea
              label="Address"
              size="sm"
              value={formData.address}
              onChange={(e) => setFormData({ ...formData, address: e.target.value })}
            />
            <Select
              label="Status"
              size="sm"
              selectedKeys={[formData.status]}
              onChange={(e) => setFormData({ ...formData, status: e.target.value as any })}
            >
              <SelectItem key="ACTIVE">ACTIVE</SelectItem>
              <SelectItem key="INACTIVE">INACTIVE</SelectItem>
            </Select>
          </ModalBody>
          <ModalFooter>
            <Button size="sm" variant="flat" onClick={onClose}>
              Cancel
            </Button>
            <Button
              size="sm"
              color="primary"
              onClick={handleSubmit}
              isLoading={isSubmitting}
              className="font-semibold"
            >
              Save
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </div>
  );
};
