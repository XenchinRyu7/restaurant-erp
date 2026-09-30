import React, { useState } from 'react';
import { Layers, Plus, Edit2, Trash2, Search } from 'lucide-react';
import {
  Table,
  TableHeader,
  TableColumn,
  TableBody,
  TableRow,
  TableCell,
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
import { ProductCategory } from '../types/erp';
import { erpApi } from '../api/erpApi';

interface CategoriesPageProps {
  categories: ProductCategory[];
  onRefresh: () => void;
}

export const CategoriesPage: React.FC<CategoriesPageProps> = ({
  categories,
  onRefresh,
}) => {
  const [search, setSearch] = useState('');
  const [editingCategory, setEditingCategory] = useState<ProductCategory | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { isOpen, onOpen, onOpenChange, onClose } = useDisclosure();

  const [formData, setFormData] = useState({
    code: '',
    name: '',
    description: '',
    status: 'ACTIVE' as 'ACTIVE' | 'INACTIVE',
  });

  const handleOpenAdd = () => {
    setEditingCategory(null);
    setFormData({
      code: `CAT-${Math.floor(100 + Math.random() * 900)}`,
      name: '',
      description: '',
      status: 'ACTIVE',
    });
    onOpen();
  };

  const handleOpenEdit = (c: ProductCategory) => {
    setEditingCategory(c);
    setFormData({
      code: c.code,
      name: c.name,
      description: c.description || '',
      status: c.status,
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
      if (editingCategory) {
        await erpApi.updateCategory(editingCategory.id, formData);
      } else {
        await erpApi.createCategory(formData);
      }
      onClose();
      onRefresh();
    } catch (err: any) {
      alert('Error saving category: ' + err.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Are you sure you want to delete this category?')) return;
    try {
      await erpApi.deleteCategory(id);
      onRefresh();
    } catch (err: any) {
      alert('Error deleting category: ' + err.message);
    }
  };

  const filtered = categories.filter(
    (c) =>
      c.name.toLowerCase().includes(search.toLowerCase()) ||
      c.code.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-4">
        <Input
          placeholder="Search categories..."
          size="sm"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          startContent={<Search className="w-4 h-4 text-neutral-400" />}
          className="max-w-sm"
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
          Add New Category
        </Button>
      </div>

      <div className="border border-neutral-200 dark:border-neutral-800 rounded-2xl bg-white dark:bg-neutral-900 shadow-sm overflow-hidden">
        <Table aria-label="Categories Table" shadow="none">
          <TableHeader>
            <TableColumn>CODE</TableColumn>
            <TableColumn>CATEGORY NAME</TableColumn>
            <TableColumn>DESCRIPTION</TableColumn>
            <TableColumn>STATUS</TableColumn>
            <TableColumn className="text-right">ACTIONS</TableColumn>
          </TableHeader>
          <TableBody emptyContent="No categories found.">
            {filtered.map((c) => (
              <TableRow key={c.id}>
                <TableCell>
                  <span className="font-mono text-xs font-semibold text-blue-600 dark:text-blue-400 bg-blue-50 dark:bg-blue-950/50 px-2 py-0.5 rounded">
                    {c.code}
                  </span>
                </TableCell>
                <TableCell className="font-bold text-xs text-neutral-900 dark:text-neutral-100">
                  {c.name}
                </TableCell>
                <TableCell className="text-xs text-neutral-500">
                  {c.description || '-'}
                </TableCell>
                <TableCell>
                  <Chip
                    size="sm"
                    variant="flat"
                    color={c.status === 'ACTIVE' ? 'success' : 'default'}
                    className="text-[10px] h-5 font-semibold"
                  >
                    {c.status}
                  </Chip>
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex items-center justify-end gap-1">
                    <Button
                      isIconOnly
                      size="sm"
                      variant="light"
                      onClick={() => handleOpenEdit(c)}
                      className="text-neutral-500 hover:text-blue-600"
                    >
                      <Edit2 className="w-3.5 h-3.5" />
                    </Button>
                    <Button
                      isIconOnly
                      size="sm"
                      variant="light"
                      onClick={() => handleDelete(c.id)}
                      className="text-neutral-500 hover:text-rose-600"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      {/* Modal */}
      <Modal isOpen={isOpen} onOpenChange={onOpenChange}>
        <ModalContent>
          <ModalHeader>
            <h3 className="text-base font-bold">
              {editingCategory ? 'Edit Category' : 'Create Category'}
            </h3>
          </ModalHeader>
          <ModalBody className="space-y-4">
            <Input
              label="Category Code"
              size="sm"
              value={formData.code}
              onChange={(e) => setFormData({ ...formData, code: e.target.value })}
              required
            />
            <Input
              label="Category Name"
              size="sm"
              placeholder="e.g. Fresh Seafood"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              required
            />
            <Textarea
              label="Description"
              size="sm"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
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
