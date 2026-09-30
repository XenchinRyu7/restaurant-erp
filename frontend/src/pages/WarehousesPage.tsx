import React, { useState } from 'react';
import { Building2, Plus, Edit2, Trash2, MapPin, Boxes } from 'lucide-react';
import {
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
import { Warehouse, InventoryStock } from '../types/erp';
import { erpApi } from '../api/erpApi';

interface WarehousesPageProps {
  warehouses: Warehouse[];
  stocks: InventoryStock[];
  onRefresh: () => void;
}

export const WarehousesPage: React.FC<WarehousesPageProps> = ({
  warehouses,
  stocks,
  onRefresh,
}) => {
  const [editingWh, setEditingWh] = useState<Warehouse | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { isOpen, onOpen, onOpenChange, onClose } = useDisclosure();

  const [formData, setFormData] = useState({
    code: '',
    name: '',
    address: '',
    status: 'ACTIVE' as 'ACTIVE' | 'INACTIVE',
  });

  const handleOpenAdd = () => {
    setEditingWh(null);
    setFormData({
      code: `WH-${Math.floor(10 + Math.random() * 90)}`,
      name: '',
      address: '',
      status: 'ACTIVE',
    });
    onOpen();
  };

  const handleOpenEdit = (w: Warehouse) => {
    setEditingWh(w);
    setFormData({
      code: w.code,
      name: w.name,
      address: w.address || '',
      status: w.status,
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
      if (editingWh) {
        await erpApi.updateWarehouse(editingWh.id, formData);
      } else {
        await erpApi.createWarehouse(formData);
      }
      onClose();
      onRefresh();
    } catch (err: any) {
      alert('Error saving warehouse: ' + err.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Are you sure you want to delete this warehouse?')) return;
    try {
      await erpApi.deleteWarehouse(id);
      onRefresh();
    } catch (err: any) {
      alert('Error deleting warehouse: ' + err.message);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h3 className="font-bold text-base text-neutral-900 dark:text-white">Storage & Hub Facilities</h3>
          <p className="text-xs text-neutral-500">Manage cold storage, dry hubs, and commissary kitchens</p>
        </div>

        <Button
          color="primary"
          size="sm"
          className="font-semibold shadow-md shadow-blue-500/20"
          startContent={<Plus className="w-4 h-4" />}
          onClick={handleOpenAdd}
        >
          Add Warehouse
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
        {warehouses.map((wh) => {
          const whStocks = stocks.filter((s) => s.warehouseId === wh.id);
          const totalUnits = whStocks.reduce((sum, s) => sum + Number(s.quantity), 0);
          const lowStockCount = whStocks.filter((s) => s.isLowStock).length;

          return (
            <Card
              key={wh.id}
              className="border border-neutral-200 dark:border-neutral-800 bg-white dark:bg-neutral-900 shadow-sm hover:shadow-md transition-all"
            >
              <CardBody className="p-5 space-y-4">
                <div className="flex items-start justify-between">
                  <div className="w-10 h-10 rounded-xl bg-blue-100 dark:bg-blue-900/40 text-blue-600 flex items-center justify-center">
                    <Building2 className="w-5 h-5" />
                  </div>

                  <div className="flex items-center gap-1">
                    <Chip
                      size="sm"
                      variant="flat"
                      color={wh.status === 'ACTIVE' ? 'success' : 'default'}
                      className="text-[10px] h-5 font-semibold"
                    >
                      {wh.status}
                    </Chip>
                    <Button
                      isIconOnly
                      size="sm"
                      variant="light"
                      onClick={() => handleOpenEdit(wh)}
                      className="text-neutral-400 hover:text-blue-600"
                    >
                      <Edit2 className="w-3.5 h-3.5" />
                    </Button>
                    <Button
                      isIconOnly
                      size="sm"
                      variant="light"
                      onClick={() => handleDelete(wh.id)}
                      className="text-neutral-400 hover:text-rose-600"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </Button>
                  </div>
                </div>

                <div>
                  <span className="font-mono text-[11px] font-semibold text-blue-600 dark:text-blue-400">
                    {wh.code}
                  </span>
                  <h4 className="font-bold text-sm text-neutral-900 dark:text-white mt-0.5">{wh.name}</h4>
                  <div className="flex items-start gap-1.5 text-xs text-neutral-500 mt-2">
                    <MapPin className="w-3.5 h-3.5 shrink-0 mt-0.5" />
                    <span className="line-clamp-2">{wh.address || 'No address specified'}</span>
                  </div>
                </div>

                <div className="pt-3 border-t border-neutral-100 dark:border-neutral-800 grid grid-cols-2 gap-3 text-center">
                  <div className="p-2 rounded-lg bg-neutral-50 dark:bg-neutral-800/40">
                    <p className="text-[11px] text-neutral-400">Products Stored</p>
                    <p className="text-base font-bold text-neutral-900 dark:text-white">{whStocks.length}</p>
                  </div>
                  <div className="p-2 rounded-lg bg-neutral-50 dark:bg-neutral-800/40">
                    <p className="text-[11px] text-neutral-400">Low Stock Items</p>
                    <p className={`text-base font-bold ${lowStockCount > 0 ? 'text-amber-500' : 'text-neutral-900 dark:text-white'}`}>
                      {lowStockCount}
                    </p>
                  </div>
                </div>
              </CardBody>
            </Card>
          );
        })}
      </div>

      {/* Modal */}
      <Modal isOpen={isOpen} onOpenChange={onOpenChange}>
        <ModalContent>
          <ModalHeader>
            <h3 className="text-base font-bold">
              {editingWh ? 'Edit Warehouse' : 'Add New Warehouse'}
            </h3>
          </ModalHeader>
          <ModalBody className="space-y-4">
            <Input
              label="Facility Code"
              size="sm"
              value={formData.code}
              onChange={(e) => setFormData({ ...formData, code: e.target.value })}
              required
            />
            <Input
              label="Facility Name"
              size="sm"
              placeholder="e.g. Cold Storage Hub Barat"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />
            <Textarea
              label="Address & Access Instructions"
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
